

package im.vector.app.features.discovery

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.session.identity.ThreePid

sealed class DiscoverySettingsAction : VectorViewModelAction {
    object RetrieveBinding : DiscoverySettingsAction()
    object Refresh : DiscoverySettingsAction()

    object DisconnectIdentityServer : DiscoverySettingsAction()
    data class ChangeIdentityServer(val url: String) : DiscoverySettingsAction()
    data class UpdateUserConsent(val newConsent: Boolean) : DiscoverySettingsAction()
    data class RevokeThreePid(val threePid: ThreePid) : DiscoverySettingsAction()
    data class ShareThreePid(val threePid: ThreePid) : DiscoverySettingsAction()
    data class FinalizeBind3pid(val threePid: ThreePid) : DiscoverySettingsAction()
    data class SubmitMsisdnToken(val threePid: ThreePid.Msisdn, val code: String) : DiscoverySettingsAction()
    data class CancelBinding(val threePid: ThreePid) : DiscoverySettingsAction()
    data class SetPoliciesExpandState(val expanded: Boolean) : DiscoverySettingsAction()
}
