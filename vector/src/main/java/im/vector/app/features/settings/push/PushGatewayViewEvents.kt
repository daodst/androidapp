

package im.vector.app.features.settings.push

import im.vector.app.core.platform.VectorViewEvents

sealed class PushGatewayViewEvents : VectorViewEvents {
    data class RemovePusherFailed(val cause: Throwable) : PushGatewayViewEvents()
}
