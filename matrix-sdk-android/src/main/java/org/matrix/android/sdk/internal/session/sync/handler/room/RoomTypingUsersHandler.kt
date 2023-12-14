

package org.matrix.android.sdk.internal.session.sync.handler.room

import io.realm.Realm
import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.session.room.membership.RoomMemberHelper
import org.matrix.android.sdk.internal.session.typing.DefaultTypingUsersTracker
import javax.inject.Inject

internal class RoomTypingUsersHandler @Inject constructor(@UserId private val userId: String,
                                                          private val typingUsersTracker: DefaultTypingUsersTracker) {

    
    fun handle(realm: Realm, roomId: String, ephemeralResult: RoomSyncHandler.EphemeralResult?) {
        val roomMemberHelper = RoomMemberHelper(realm, roomId)
        val typingIds = ephemeralResult?.typingUserIds?.filter { it != userId }.orEmpty()
        val senderInfo = typingIds.map { userId ->
            val roomMemberSummaryEntity = roomMemberHelper.getLastRoomMember(userId)
            SenderInfo(
                    userId = userId,
                    displayName = roomMemberSummaryEntity?.displayName,
                    isUniqueDisplayName = roomMemberHelper.isUniqueDisplayName(roomMemberSummaryEntity?.displayName),
                    avatarUrl = roomMemberSummaryEntity?.avatarUrl
            )
        }
        typingUsersTracker.setTypingUsersFromRoom(roomId, senderInfo)
    }
}
