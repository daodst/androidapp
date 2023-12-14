

package im.vector.app.features.settings.legals

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.features.discovery.ServerAndPolicies

data class LegalsState(
        val homeServer: Async<ServerAndPolicies?> = Uninitialized,
        val hasIdentityServer: Boolean = false,
        val identityServer: Async<ServerAndPolicies?> = Uninitialized
) : MavericksState
