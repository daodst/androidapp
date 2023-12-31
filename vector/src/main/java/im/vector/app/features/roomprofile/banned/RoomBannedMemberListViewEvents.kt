

package im.vector.app.features.roomprofile.banned

import im.vector.app.core.platform.VectorViewEvents
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary

sealed class RoomBannedMemberListViewEvents : VectorViewEvents {
    data class ShowBannedInfo(val bannedByUserId: String, val banReason: String, val roomMemberSummary: RoomMemberSummary) : RoomBannedMemberListViewEvents()
    data class ToastError(val info: String) : RoomBannedMemberListViewEvents()
}
