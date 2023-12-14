

package im.vector.app.features.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.nulabinc.zxcvbn.Strength
import im.vector.app.core.platform.WaitingViewData
import im.vector.app.features.crypto.recover.BootstrapStep
import im.vector.app.features.crypto.recover.SetupMode
import org.matrix.android.sdk.api.session.securestorage.SsssKeyCreationInfo

data class BootstrapNewViewState(
        
        val setupMode: SetupMode = SetupMode.NORMAL,
        val step: BootstrapStep = BootstrapStep.CheckingMigration,
        val passphrase: String? = null,
        val migrationRecoveryKey: String? = null,
        val passphraseRepeat: String? = null,
        val crossSigningInitialization: Async<Unit> = Uninitialized,
        val passphraseStrength: Async<Strength> = Uninitialized,
        val passphraseConfirmMatch: Async<Unit> = Uninitialized,
        val recoveryKeyCreationInfo: SsssKeyCreationInfo? = null,
        val initializationWaitingViewData: WaitingViewData? = null,
        val recoverySaveFileProcess: Async<Unit> = Uninitialized,
        val privateKey: String? = null
) : MavericksState {

}
