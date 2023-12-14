

package im.vector.app.features.discovery.change

import com.airbnb.mvrx.MavericksState

data class SetIdentityServerState(
        val homeServerUrl: String = "",
        
        val defaultIdentityServerUrl: String? = null
) : MavericksState
