

package im.vector.app.features.analytics.extensions

import im.vector.app.features.analytics.plan.JoinedRoom
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoom

fun Int?.toAnalyticsRoomSize(): JoinedRoom.RoomSize {
    return when (this) {
        null,
        2            -> JoinedRoom.RoomSize.Two
        in 3..10     -> JoinedRoom.RoomSize.ThreeToTen
        in 11..100   -> JoinedRoom.RoomSize.ElevenToOneHundred
        in 101..1000 -> JoinedRoom.RoomSize.OneHundredAndOneToAThousand
        else         -> JoinedRoom.RoomSize.MoreThanAThousand
    }
}

fun RoomSummary?.toAnalyticsJoinedRoom(): JoinedRoom {
    return JoinedRoom(
            isDM = this?.isDirect.orFalse(),
            isSpace = this?.roomType == RoomType.SPACE,
            roomSize = this?.joinedMembersCount?.toAnalyticsRoomSize() ?: JoinedRoom.RoomSize.Two
    )
}

fun PublicRoom.toAnalyticsJoinedRoom(): JoinedRoom {
    return JoinedRoom(
            isDM = false,
            isSpace = false,
            roomSize = numJoinedMembers.toAnalyticsRoomSize()
    )
}
