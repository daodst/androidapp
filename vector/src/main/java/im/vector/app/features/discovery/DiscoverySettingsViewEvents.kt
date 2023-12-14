

package im.vector.app.features.discovery

import im.vector.app.core.platform.VectorViewEvents

sealed class DiscoverySettingsViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : DiscoverySettingsViewEvents()
}
