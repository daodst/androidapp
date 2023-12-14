

package im.vector.app.features.home.room.list

import im.vector.app.core.platform.VectorViewEvents
import org.matrix.android.sdk.api.session.room.model.RoomSummary


sealed class RoomListViewEvents : VectorViewEvents {
    data class Loading(val message: CharSequence? = null) : RoomListViewEvents()
    data class Failure(val throwable: Throwable) : RoomListViewEvents()

    data class SelectRoom(val roomSummary: RoomSummary, val isInviteAlreadyAccepted: Boolean = false) : RoomListViewEvents()
    object Done : RoomListViewEvents()
    data class NavigateToMxToBottomSheet(val link: String) : RoomListViewEvents()
    data class SendWidgetBroadCast(val data: String): RoomListViewEvents()
}
