

package im.vector.app.features

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface VectorOverrides {
    val forceDialPad: Flow<Boolean>
    val forceLoginFallback: Flow<Boolean>
    val forceHomeserverCapabilities: Flow<HomeserverCapabilitiesOverride>?
}

data class HomeserverCapabilitiesOverride(
        val canChangeDisplayName: Boolean?,
        val canChangeAvatar: Boolean?
)

class DefaultVectorOverrides : VectorOverrides {
    override val forceDialPad = flowOf(false)
    override val forceLoginFallback = flowOf(false)
    override val forceHomeserverCapabilities: Flow<HomeserverCapabilitiesOverride>? = null
}
