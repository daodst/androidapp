

package im.vector.app.features.roomprofile.notifications

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.session.room.notification.RoomNotificationState

sealed class RoomNotificationSettingsAction : VectorViewModelAction {
    data class SelectNotificationState(val notificationState: RoomNotificationState) : RoomNotificationSettingsAction()
}
