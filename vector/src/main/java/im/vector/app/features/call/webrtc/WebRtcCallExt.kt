

package im.vector.app.features.call.webrtc

import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.api.util.toMatrixItem

fun WebRtcCall.getOpponentAsMatrixItem(session: Session): MatrixItem? {
    return session.getRoom(nativeRoomId)?.let { room ->
        val roomSummary = room.roomSummary() ?: return@let null
        
        if (roomSummary.otherMemberIds.isEmpty().orFalse()) {
            roomSummary.toMatrixItem()
        } else {
            val userId = roomSummary.otherMemberIds.first()
            return room.getRoomMember(userId)?.toMatrixItem()
        }
    }
}
