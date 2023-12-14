

package im.vector.app.features.settings.threepids

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.session.identity.ThreePid

sealed class ThreePidsSettingsAction : VectorViewModelAction {
    data class ChangeUiState(val newUiState: ThreePidsSettingsUiState) : ThreePidsSettingsAction()
    data class AddThreePid(val threePid: ThreePid) : ThreePidsSettingsAction()
    data class SubmitCode(val threePid: ThreePid.Msisdn, val code: String) : ThreePidsSettingsAction()
    data class ContinueThreePid(val threePid: ThreePid) : ThreePidsSettingsAction()
    data class CancelThreePid(val threePid: ThreePid) : ThreePidsSettingsAction()

    
    data class DeleteThreePid(val threePid: ThreePid) : ThreePidsSettingsAction()

    object SsoAuthDone : ThreePidsSettingsAction()
    data class PasswordAuthDone(val password: String) : ThreePidsSettingsAction()
    object ReAuthCancelled : ThreePidsSettingsAction()
}
