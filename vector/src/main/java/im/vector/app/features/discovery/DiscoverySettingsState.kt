

package im.vector.app.features.discovery

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized

data class DiscoverySettingsState(
        val identityServer: Async<ServerAndPolicies?> = Uninitialized,
        val emailList: Async<List<PidInfo>> = Uninitialized,
        val phoneNumbersList: Async<List<PidInfo>> = Uninitialized,
        
        val termsNotSigned: Boolean = false,
        val userConsent: Boolean = false,
        val isIdentityPolicyUrlsExpanded: Boolean = false
) : MavericksState
