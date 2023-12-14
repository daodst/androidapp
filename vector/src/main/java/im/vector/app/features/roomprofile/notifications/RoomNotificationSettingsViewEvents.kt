

package im.vector.app.features.roomprofile.notifications

import im.vector.app.core.platform.VectorViewEvents

sealed class RoomNotificationSettingsViewEvents : VectorViewEvents {
    object Loading : RoomNotificationSettingsViewEvents()
    object Done : RoomNotificationSettingsViewEvents()
    data class Failure(val throwable: Throwable) : RoomNotificationSettingsViewEvents()
}
