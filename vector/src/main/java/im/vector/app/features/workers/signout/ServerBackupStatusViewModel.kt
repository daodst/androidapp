

package im.vector.app.features.workers.signout

import androidx.lifecycle.MutableLiveData
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Uninitialized
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.platform.EmptyAction
import im.vector.app.core.platform.EmptyViewEvents
import im.vector.app.core.platform.VectorViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.crosssigning.MASTER_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.SELF_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.USER_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupState
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupStateListener
import org.matrix.android.sdk.flow.flow

data class ServerBackupStatusViewState(
        val bannerState: Async<BannerState> = Uninitialized
) : MavericksState


sealed class BannerState {

    object Hidden : BannerState()

    
    data class Setup(val numberOfKeys: Int) : BannerState()

    
    object BackingUp : BannerState()
}

class ServerBackupStatusViewModel @AssistedInject constructor(@Assisted initialState: ServerBackupStatusViewState,
                                                              private val session: Session) :
        VectorViewModel<ServerBackupStatusViewState, EmptyAction, EmptyViewEvents>(initialState), KeysBackupStateListener {

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<ServerBackupStatusViewModel, ServerBackupStatusViewState> {
        override fun create(initialState: ServerBackupStatusViewState): ServerBackupStatusViewModel
    }

    companion object : MavericksViewModelFactory<ServerBackupStatusViewModel, ServerBackupStatusViewState> by hiltMavericksViewModelFactory()

    
    val keysExportedToFile = MutableLiveData<Boolean>()
    val keysBackupState = MutableLiveData<KeysBackupState>()

    private val keyBackupFlow = MutableSharedFlow<KeysBackupState>(0)

    init {
        session.cryptoService().keysBackupService().addListener(this)
        keysBackupState.value = session.cryptoService().keysBackupService().state
        val liveUserAccountData = session.flow().liveUserAccountData(setOf(MASTER_KEY_SSSS_NAME, USER_SIGNING_KEY_SSSS_NAME, SELF_SIGNING_KEY_SSSS_NAME))
        val liveCrossSigningInfo = session.flow().liveCrossSigningInfo(session.myUserId)
        val liveCrossSigningPrivateKeys = session.flow().liveCrossSigningPrivateKeys()
        combine(liveUserAccountData, liveCrossSigningInfo, keyBackupFlow, liveCrossSigningPrivateKeys) { _, crossSigningInfo, keyBackupState, pInfo ->
            
            if (session.sharedSecretStorageService.isRecoverySetup()) {
                
                return@combine when (keyBackupState) {
                    KeysBackupState.BackingUp -> BannerState.BackingUp
                    else                      -> BannerState.Hidden
                }
            }

            
            
            if (
                    crossSigningInfo.getOrNull() == null ||
                    (crossSigningInfo.getOrNull()?.isTrusted() == true &&
                            pInfo.getOrNull()?.allKnown().orFalse())
            ) {
                
                return@combine BannerState.Setup(numberOfKeys = getNumberOfKeysToBackup())
            }
            BannerState.Hidden
        }
                .sample(1000) 
                .distinctUntilChanged()
                .execute { async ->
                    copy(
                            bannerState = async
                    )
                }

        viewModelScope.launch {
            keyBackupFlow.tryEmit(session.cryptoService().keysBackupService().state)
        }
    }

    
    fun getCurrentBackupVersion(): String {
        return session.cryptoService().keysBackupService().currentBackupVersion ?: ""
    }

    
    fun getNumberOfKeysToBackup(): Int {
        return session.cryptoService().inboundGroupSessionsCount(false)
    }

    
    fun canRestoreKeys(): Boolean {
        return session.cryptoService().keysBackupService().canRestoreKeys()
    }

    override fun onCleared() {
        session.cryptoService().keysBackupService().removeListener(this)
        super.onCleared()
    }

    override fun onStateChange(newState: KeysBackupState) {
        viewModelScope.launch {
            keyBackupFlow.tryEmit(session.cryptoService().keysBackupService().state)
        }
        keysBackupState.value = newState
    }

    fun refreshRemoteStateIfNeeded() {
        if (keysBackupState.value == KeysBackupState.Disabled) {
            session.cryptoService().keysBackupService().checkAndStartKeysBackup()
        }
    }

    override fun handle(action: EmptyAction) {}
}
