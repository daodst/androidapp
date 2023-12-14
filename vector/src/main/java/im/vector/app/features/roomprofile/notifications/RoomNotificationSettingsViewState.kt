

package im.vector.app.features.roomprofile.notifications

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import im.vector.app.features.home.room.list.actions.RoomListActionsArgs
import im.vector.app.features.roomprofile.RoomProfileArgs
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.notification.RoomNotificationState

data class RoomNotificationSettingsViewState(
        val roomId: String,
        val roomSummary: Async<RoomSummary> = Uninitialized,
        val isLoading: Boolean = false,
        val notificationState: Async<RoomNotificationState> = Uninitialized
) : MavericksState {
    constructor(args: RoomProfileArgs) : this(roomId = args.roomId)
    constructor(args: RoomListActionsArgs) : this(roomId = args.roomId)
}


val RoomNotificationSettingsViewState.notificationStateMapped: Async<RoomNotificationState>
    get() {
        return when {
            
            (roomSummary()?.isEncrypted == true && notificationState() == RoomNotificationState.MENTIONS_ONLY)
                                                                      -> Success(RoomNotificationState.MUTE)
            notificationState() == RoomNotificationState.ALL_MESSAGES -> Success(RoomNotificationState.ALL_MESSAGES_NOISY)
            else                                                      -> notificationState
        }
    }


val RoomNotificationSettingsViewState.notificationOptions: List<RoomNotificationState>
    get() {
        return if (roomSummary()?.isEncrypted == true) {
            listOf(RoomNotificationState.ALL_MESSAGES_NOISY, RoomNotificationState.MUTE)
        } else {
            listOf(RoomNotificationState.ALL_MESSAGES_NOISY, RoomNotificationState.MENTIONS_ONLY, RoomNotificationState.MUTE)
        }
    }
