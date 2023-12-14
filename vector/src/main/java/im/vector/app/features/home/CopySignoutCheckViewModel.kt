

package im.vector.app.features.home

import android.net.Uri
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.platform.EmptyViewEvents
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.features.crypto.keys.KeysExporter
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.crosssigning.MASTER_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.SELF_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.USER_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupState
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupStateListener
import org.matrix.android.sdk.flow.flow
import timber.log.Timber

data class SignoutCheckViewState(
        val userId: String = "",
        val backupIsSetup: Boolean = false,
        val crossSigningSetupAllKeysKnown: Boolean = false,
        val keysBackupState: KeysBackupState = KeysBackupState.Unknown,
        val hasBeenExportedToFile: Async<Boolean> = Uninitialized
) : MavericksState

class CopySignoutCheckViewModel @AssistedInject constructor(
        @Assisted initialState: SignoutCheckViewState,
        private val session: Session,
        private val keysExporter: KeysExporter
) : VectorViewModel<SignoutCheckViewState, CopySignoutCheckViewModel.Actions, EmptyViewEvents>(initialState), KeysBackupStateListener {

    sealed class Actions : VectorViewModelAction {
        data class ExportKeys(val passphrase: String, val uri: Uri) : Actions()
        object KeySuccessfullyManuallyExported : Actions()
        object ViewStarted : Actions()
    }

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<CopySignoutCheckViewModel, SignoutCheckViewState> {
        override fun create(initialState: SignoutCheckViewState): CopySignoutCheckViewModel
    }

    companion object : MavericksViewModelFactory<CopySignoutCheckViewModel, SignoutCheckViewState> by hiltMavericksViewModelFactory()

    fun initialize() {
        session.cryptoService().keysBackupService().addListener(this)
        session.cryptoService().keysBackupService().checkAndStartKeysBackup()

        val quad4SIsSetup = session.sharedSecretStorageService.isRecoverySetup()
        val allKeysKnown = session.cryptoService().crossSigningService().allPrivateKeysKnown()
        val backupState = session.cryptoService().keysBackupService().state
        setState {
            copy(
                    userId = session.myUserId,
                    crossSigningSetupAllKeysKnown = allKeysKnown,
                    backupIsSetup = quad4SIsSetup,
                    keysBackupState = backupState
            )
        }

        session.flow().liveUserAccountData(setOf(MASTER_KEY_SSSS_NAME, USER_SIGNING_KEY_SSSS_NAME, SELF_SIGNING_KEY_SSSS_NAME))
                .map {
                    session.sharedSecretStorageService.isRecoverySetup()
                }
                .distinctUntilChanged()
                .execute {
                    copy(backupIsSetup = it.invoke() == true)
                }
    }

    override fun onCleared() {
        session.cryptoService().keysBackupService().removeListener(this)
        super.onCleared()
    }

    override fun onStateChange(newState: KeysBackupState) {
        setState {
            copy(
                    keysBackupState = newState
            )
        }
    }

    fun refreshRemoteStateIfNeeded() = withState { state ->
        if (state.keysBackupState == KeysBackupState.Disabled) {
            session.cryptoService().keysBackupService().checkAndStartKeysBackup()
        }
    }

    override fun handle(action: Actions) {
        when (action) {
            is Actions.ExportKeys                   -> handleExportKeys(action)
            Actions.KeySuccessfullyManuallyExported -> {
                setState {
                    copy(hasBeenExportedToFile = Success(true))
                }
            }
            Actions.ViewStarted                     -> {
                initialize()
            }
        }
    }

    private fun handleExportKeys(action: Actions.ExportKeys) {
        setState {
            copy(hasBeenExportedToFile = Loading())
        }

        viewModelScope.launch {
            val newState = try {
                keysExporter.export(action.passphrase, action.uri)
                Success(true)
            } catch (failure: Throwable) {
                Timber.e("## Failed to export manually keys ${failure.localizedMessage}")
                Uninitialized
            }

            setState {
                copy(hasBeenExportedToFile = newState)
            }
        }
    }
}
