

package im.vector.app.features.home.room.list.actions

import im.vector.app.features.roomprofile.notifications.RoomNotificationSettingsViewState

data class RoomListQuickActionViewState(
        val roomListActionsArgs: RoomListActionsArgs,
        val notificationSettingsViewState: RoomNotificationSettingsViewState
)
