

package org.matrix.android.sdk.internal.database.mapper

import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.internal.database.model.RoomMemberSummaryEntity
import org.matrix.android.sdk.internal.database.model.presence.toUserPresence

internal object RoomMemberSummaryMapper {

    fun map(roomMemberSummaryEntity: RoomMemberSummaryEntity): RoomMemberSummary {
        return RoomMemberSummary(
                userId = roomMemberSummaryEntity.userId,
                userPresence = roomMemberSummaryEntity.userPresenceEntity?.toUserPresence(),
                avatarUrl = roomMemberSummaryEntity.avatarUrl,
                displayName = roomMemberSummaryEntity.displayName,
                membership = roomMemberSummaryEntity.membership
        )
    }
}

internal fun RoomMemberSummaryEntity.asDomain(): RoomMemberSummary {
    return RoomMemberSummaryMapper.map(this)
}
