

package im.vector.app.features.home

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.nulabinc.zxcvbn.Zxcvbn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.R
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.platform.WaitingViewData
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.auth.ReAuthActivity
import im.vector.app.features.crypto.recover.BackupToQuadSMigrationTask
import im.vector.app.features.crypto.recover.BootstrapActions
import im.vector.app.features.crypto.recover.BootstrapCrossSigningTask
import im.vector.app.features.crypto.recover.BootstrapProgressListener
import im.vector.app.features.crypto.recover.BootstrapResult
import im.vector.app.features.crypto.recover.BootstrapStep
import im.vector.app.features.crypto.recover.BootstrapViewEvents
import im.vector.app.features.crypto.recover.Params
import im.vector.app.features.crypto.recover.SetupMode
import im.vector.app.features.crypto.recover.formatRecoveryKey
import im.vector.app.features.login.ReAuthHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.UserPasswordAuth
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.api.auth.registration.nextUncompletedStage
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupLastVersionResult
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysVersionResult
import org.matrix.android.sdk.api.session.crypto.keysbackup.extractCurveKeyFromRecoveryKey
import org.matrix.android.sdk.api.session.crypto.keysbackup.toKeysVersionResult
import org.matrix.android.sdk.api.session.securestorage.RawBytesKeySpec
import org.matrix.android.sdk.api.session.uia.DefaultBaseAuth
import org.matrix.android.sdk.api.util.awaitCallback
import org.matrix.android.sdk.api.util.fromBase64
import timber.log.Timber
import java.io.OutputStream
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class BootstrapViewModel @AssistedInject constructor(
        @Assisted initialState: BootstrapNewViewState,
        private val stringProvider: StringProvider,
        private val errorFormatter: ErrorFormatter,
        private val session: Session,
        private val reAuthHelper: ReAuthHelper,
        private val bootstrapTask: BootstrapCrossSigningTask,
        private val migrationTask: BackupToQuadSMigrationTask,
) : VectorViewModel<BootstrapNewViewState, BootstrapActions, BootstrapViewEvents>(initialState) {

    private var doesKeyBackupExist: Boolean = false
    private var isBackupCreatedFromPassphrase: Boolean = false
    private val zxcvbn = Zxcvbn()

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<BootstrapViewModel, BootstrapNewViewState> {
        override fun create(initialState: BootstrapNewViewState): BootstrapViewModel
    }

    companion object : MavericksViewModelFactory<BootstrapViewModel, BootstrapNewViewState> by hiltMavericksViewModelFactory()


    var uiaContinuation: Continuation<UIABaseAuth>? = null
    var pendingAuth: UIABaseAuth? = null

    init {

    }

    fun setPrivateKey(privateKey: String) {
        setState {
            copy(privateKey = privateKey)
        }
    }

    fun initStatus(state: BootstrapNewViewState) {
        when (state.setupMode) {
            SetupMode.PASSPHRASE_RESET,
            SetupMode.PASSPHRASE_AND_NEEDED_SECRETS_RESET,
            SetupMode.HARD_RESET         -> {
                setState {
                    copy(step = BootstrapStep.FirstForm(keyBackUpExist = false, reset = true))
                }
            }
            SetupMode.CROSS_SIGNING_ONLY -> {
                
                setState {
                    copy(step = BootstrapStep.AccountReAuth())
                }
            }
            SetupMode.NORMAL             -> {
                
                setState {
                    copy(step = BootstrapStep.CheckingMigration)
                }

                
                viewModelScope.launch(Dispatchers.IO) {
                    val version = awaitCallback<KeysBackupLastVersionResult> {
                        session.cryptoService().keysBackupService().getCurrentVersion(it)
                    }.toKeysVersionResult()
                    if (version == null) {
                        
                        doesKeyBackupExist = false
                        setState {
                            copy(step = BootstrapStep.FirstForm(keyBackUpExist = doesKeyBackupExist))
                        }
                    } else {
                        
                        val keyVersion = awaitCallback<KeysVersionResult?> {
                            session.cryptoService().keysBackupService().getVersion(version.version, it)
                        }
                        if (keyVersion == null) {
                            
                            _viewEvents.post(BootstrapViewEvents.Dismiss(false))
                        } else {
                            doesKeyBackupExist = true
                            isBackupCreatedFromPassphrase = keyVersion.getAuthDataAsMegolmBackupAuthData()?.privateKeySalt != null
                            setState {
                                copy(step = BootstrapStep.FirstForm(keyBackUpExist = doesKeyBackupExist))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleStartMigratingKeyBackup() {
        if (isBackupCreatedFromPassphrase) {
            setState {
                copy(step = BootstrapStep.GetBackupSecretPassForMigration(useKey = false))
            }
        } else {
            setState {
                copy(step = BootstrapStep.GetBackupSecretKeyForMigration)
            }
        }
    }

    override fun handle(action: BootstrapActions) = withState { state ->
        when (action) {
            is BootstrapActions.GoBack                           -> queryBack()
            BootstrapActions.StartKeyBackupMigration             -> {
                handleStartMigratingKeyBackup()
            }
            is BootstrapActions.Start                            -> {
                handleStart(action)
            }
            is BootstrapActions.UpdateCandidatePassphrase        -> {
                val strength = zxcvbn.measure(action.pass)
                setState {
                    copy(
                            passphrase = action.pass,
                            passphraseStrength = Success(strength)
                    )
                }
            }
            is BootstrapActions.GoToConfirmPassphrase            -> {
                setState {
                    copy(
                            passphrase = action.passphrase,
                            step = BootstrapStep.ConfirmPassphrase
                    )
                }
            }
            is BootstrapActions.UpdateConfirmCandidatePassphrase -> {
                setState {
                    copy(
                            passphraseRepeat = action.pass
                    )
                }
            }
            is BootstrapActions.DoInitialize                     -> {
                if (state.passphrase == state.passphraseRepeat) {
                    startInitializeFlow(state)
                } else {
                    setState {
                        copy(
                                passphraseConfirmMatch = Fail(Throwable(stringProvider.getString(R.string.passphrase_passphrase_does_not_match)))
                        )
                    }
                }
            }
            is BootstrapActions.DoInitializeGeneratedKey         -> {
                startInitializeFlow(state)
            }
            BootstrapActions.RecoveryKeySaved                    -> {
                _viewEvents.post(BootstrapViewEvents.RecoveryKeySaved)
                setState {
                    copy(step = BootstrapStep.SaveRecoveryKey(true))
                }
            }
            BootstrapActions.Completed                           -> {
                _viewEvents.post(BootstrapViewEvents.Dismiss(true))
            }
            BootstrapActions.GoToCompleted                       -> {
                setState {
                    copy(step = BootstrapStep.DoneSuccess)
                }
            }
            BootstrapActions.SaveReqQueryStarted                 -> {
                setState {
                    copy(recoverySaveFileProcess = Loading())
                }
            }
            is BootstrapActions.SaveKeyToUri                     -> {
                saveRecoveryKeyToUri(action.os)
            }
            BootstrapActions.SaveReqFailed                       -> {
                setState {
                    copy(recoverySaveFileProcess = Uninitialized)
                }
            }
            BootstrapActions.GoToEnterAccountPassword            -> {
                setState {
                    copy(step = BootstrapStep.AccountReAuth())
                }
            }
            BootstrapActions.HandleForgotBackupPassphrase        -> {
                if (state.step is BootstrapStep.GetBackupSecretPassForMigration) {
                    setState {
                        copy(step = BootstrapStep.GetBackupSecretPassForMigration(true))
                    }
                } else return@withState
            }
            is BootstrapActions.DoMigrateWithPassphrase          -> {
                startMigrationFlow(state.step, action.passphrase, null)
            }
            is BootstrapActions.DoMigrateWithRecoveryKey         -> {
                startMigrationFlow(state.step, null, action.recoveryKey)
            }
            BootstrapActions.SsoAuthDone                         -> {
                uiaContinuation?.resume(DefaultBaseAuth(session = pendingAuth?.session ?: ""))
            }
            is BootstrapActions.PasswordAuthDone                 -> {
                val decryptedPass = session.loadSecureSecret<String>(action.password.fromBase64().inputStream(), ReAuthActivity.DEFAULT_RESULT_KEYSTORE_ALIAS)
                uiaContinuation?.resume(
                        UserPasswordAuth(
                                session = pendingAuth?.session,
                                password = decryptedPass,
                                user = session.myUserId
                        )
                )
            }
            BootstrapActions.ReAuthCancelled                     -> {
                setState {
                    copy(step = BootstrapStep.AccountReAuth(stringProvider.getString(R.string.authentication_error)))
                }
            }
        }
    }

    private fun handleStart(action: BootstrapActions.Start) = withState {
        if (action.userWantsToEnterPassphrase) {
            setState {
                copy(
                        step = BootstrapStep.SetupPassphrase
                )
            }
        } else {
            startInitializeFlow(it)
        }
    }

    
    
    
    private fun saveRecoveryKeyToUri(os: OutputStream) = withState { state ->
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                os.use {
                    os.write((state.recoveryKeyCreationInfo?.recoveryKey?.formatRecoveryKey() ?: "").toByteArray())
                }
            }.fold({
                setState {
                    _viewEvents.post(BootstrapViewEvents.RecoveryKeySaved)
                    copy(
                            recoverySaveFileProcess = Success(Unit),
                            step = BootstrapStep.SaveRecoveryKey(isSaved = true)
                    )
                }
            }, {
                setState {
                    copy(recoverySaveFileProcess = Fail(it))
                }
            })
        }
    }

    private fun startMigrationFlow(previousStep: BootstrapStep, passphrase: String?, recoveryKey: String?) { 
        setState {
            copy(step = BootstrapStep.Initializing)
        }
        viewModelScope.launch(Dispatchers.IO) {
            val progressListener = object : BootstrapProgressListener {
                override fun onProgress(data: WaitingViewData) {
                    setState {
                        copy(
                                initializationWaitingViewData = data
                        )
                    }
                }
            }
            migrationTask.invoke(this, BackupToQuadSMigrationTask.Params(passphrase, recoveryKey, progressListener)) {
                when (it) {
                    is BackupToQuadSMigrationTask.Result.Success -> {
                        setState {
                            copy(
                                    passphrase = passphrase,
                                    passphraseRepeat = passphrase,
                                    migrationRecoveryKey = recoveryKey
                            )
                        }
                        withState { startInitializeFlow(it) }
                    }
                    is BackupToQuadSMigrationTask.Result.Failure -> {
                        _viewEvents.post(
                                BootstrapViewEvents.ModalError(it.toHumanReadable())
                        )
                        setState {
                            copy(
                                    step = previousStep
                            )
                        }
                    }
                }
            }
        }
    }

    private fun startInitializeFlow(state: BootstrapNewViewState) {
        val previousStep = state.step

        setState {
            copy(step = BootstrapStep.Initializing)
        }

        val progressListener = object : BootstrapProgressListener {
            override fun onProgress(data: WaitingViewData) {
                setState {
                    copy(
                            initializationWaitingViewData = data
                    )
                }
            }
        }

        val interceptor = object : UserInteractiveAuthInterceptor {
            override fun performStage(flowResponse: RegistrationFlowResponse, errCode: String?, promise: Continuation<UIABaseAuth>) {
                when (flowResponse.nextUncompletedStage()) {
                    LoginFlowTypes.PASSWORD -> {
                        pendingAuth = UserPasswordAuth(
                                
                                session = flowResponse.session,
                                user = session.myUserId,
                                password = null
                        )
                        uiaContinuation = promise
                        setState {
                            copy(
                                    step = BootstrapStep.AccountReAuth()
                            )
                        }
                        _viewEvents.post(BootstrapViewEvents.RequestReAuth(flowResponse, errCode))
                    }
                    LoginFlowTypes.SSO      -> {
                        pendingAuth = DefaultBaseAuth(flowResponse.session)
                        uiaContinuation = promise
                        setState {
                            copy(
                                    step = BootstrapStep.AccountReAuth()
                            )
                        }
                        _viewEvents.post(BootstrapViewEvents.RequestReAuth(flowResponse, errCode))
                    }
                    else                    -> {
                        Timber.i("=======BootstrapViewModel========================UserInteractiveAuthInterceptor=========${System.currentTimeMillis()}===========")
                    }
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            bootstrapTask.invoke(
                    this,
                    Params(
                            userInteractiveAuthInterceptor = interceptor,
                            progressListener = progressListener,
                            passphrase = state.passphrase,
                            keySpec = state.migrationRecoveryKey?.let {
                                extractCurveKeyFromRecoveryKey(it)?.let { RawBytesKeySpec(it) }
                            },
                            setupMode = state.setupMode
                    )
            ) { bootstrapResult ->
                when (bootstrapResult) {
                    is BootstrapResult.SuccessCrossSigningOnly -> {
                        _viewEvents.post(BootstrapViewEvents.Dismiss(true))
                    }
                    is BootstrapResult.Success                 -> {
                        setState {
                            copy(
                                    recoveryKeyCreationInfo = bootstrapResult.keyInfo,
                                    step = BootstrapStep.SaveRecoveryKey(
                                            
                                            state.passphrase != null
                                    )
                            )
                        }
                    }
                    is BootstrapResult.InvalidPasswordError    -> {
                        
                        setState {
                            copy(
                                    step = BootstrapStep.AccountReAuth(stringProvider.getString(R.string.auth_invalid_login_param))
                            )
                        }
                    }
                    is BootstrapResult.Failure                 -> {
                        if (bootstrapResult is BootstrapResult.GenericError &&
                                bootstrapResult.failure is Failure.OtherServerError &&
                                bootstrapResult.failure.httpCode == 401) {
                            
                        } else {
                            _viewEvents.post(BootstrapViewEvents.ModalError(bootstrapResult.error ?: stringProvider.getString(R.string.matrix_error)))
                            
                            setState {
                                copy(
                                        step = previousStep
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    
    
    

    private fun queryBack() = withState { state ->
        when (state.step) {
            is BootstrapStep.GetBackupSecretPassForMigration -> {
                if (state.step.useKey) {
                    
                    setState {
                        copy(
                                step = BootstrapStep.GetBackupSecretPassForMigration(
                                        useKey = false
                                )
                        )
                    }
                } else {
                    setState {
                        copy(
                                step = BootstrapStep.FirstForm(keyBackUpExist = doesKeyBackupExist),
                                
                                passphrase = null,
                                passphraseRepeat = null,
                                
                                migrationRecoveryKey = null
                        )
                    }
                }
            }
            is BootstrapStep.SetupPassphrase                 -> {
                setState {
                    copy(
                            step = BootstrapStep.FirstForm(keyBackUpExist = doesKeyBackupExist),
                            
                            passphrase = null,
                            passphraseRepeat = null
                    )
                }
            }
            is BootstrapStep.ConfirmPassphrase               -> {
                setState {
                    copy(
                            step = BootstrapStep.SetupPassphrase
                    )
                }
            }
            is BootstrapStep.AccountReAuth                   -> {
                _viewEvents.post(BootstrapViewEvents.SkipBootstrap(state.passphrase != null))
            }
            BootstrapStep.Initializing                       -> {
                
                _viewEvents.post(BootstrapViewEvents.SkipBootstrap(state.passphrase != null))
            }
            is BootstrapStep.SaveRecoveryKey,
            BootstrapStep.DoneSuccess                        -> {
                
            }
            BootstrapStep.CheckingMigration                  -> Unit
            is BootstrapStep.FirstForm                       -> {
                _viewEvents.post(
                        when (state.setupMode) {
                            SetupMode.CROSS_SIGNING_ONLY,
                            SetupMode.NORMAL -> BootstrapViewEvents.SkipBootstrap()
                            else             -> BootstrapViewEvents.Dismiss(success = false)
                        }
                )
            }
            is BootstrapStep.GetBackupSecretForMigration     -> {
                setState {
                    copy(
                            step = BootstrapStep.FirstForm(keyBackUpExist = doesKeyBackupExist),
                            
                            passphrase = null,
                            passphraseRepeat = null,
                            
                            migrationRecoveryKey = null
                    )
                }
            }
        }
    }

    private fun BackupToQuadSMigrationTask.Result.Failure.toHumanReadable(): String {
        return when (this) {
            is BackupToQuadSMigrationTask.Result.InvalidRecoverySecret -> stringProvider.getString(R.string.keys_backup_passphrase_error_decrypt)
            is BackupToQuadSMigrationTask.Result.ErrorFailure          -> errorFormatter.toHumanReadable(throwable)
            
            
            else                                                       -> stringProvider.getString(R.string.unexpected_error)
        }
    }
}
