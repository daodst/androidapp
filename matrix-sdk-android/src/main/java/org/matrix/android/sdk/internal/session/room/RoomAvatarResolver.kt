

package org.matrix.android.sdk.internal.session.room

import io.realm.Realm
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.RoomAvatarContent
import org.matrix.android.sdk.internal.database.mapper.asDomain
import org.matrix.android.sdk.internal.database.model.CurrentStateEventEntity
import org.matrix.android.sdk.internal.database.model.RoomMemberSummaryEntityFields
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntity
import org.matrix.android.sdk.internal.database.query.getOrNull
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.session.room.membership.RoomMemberHelper
import javax.inject.Inject

internal class RoomAvatarResolver @Inject constructor(@UserId private val userId: String) {

    
    fun resolve(realm: Realm, roomId: String): String? {
        val roomName = CurrentStateEventEntity.getOrNull(realm, roomId, type = EventType.STATE_ROOM_AVATAR, stateKey = "")
                ?.root
                ?.asDomain()
                ?.content
                ?.toModel<RoomAvatarContent>()
                ?.avatarUrl
        if (!roomName.isNullOrEmpty()) {
            return roomName
        }
        val roomMembers = RoomMemberHelper(realm, roomId)
        val members = roomMembers.queryActiveRoomMembersEvent().findAll()
        
        val isDirectRoom = RoomSummaryEntity.where(realm, roomId).findFirst()?.isDirect.orFalse()

        if (isDirectRoom) {
            if (members.size == 1) {
                
                val firstLeftAvatarUrl = roomMembers.queryLeftRoomMembersEvent()
                        .findAll()
                        .firstOrNull { !it.avatarUrl.isNullOrEmpty() }
                        ?.avatarUrl

                return firstLeftAvatarUrl ?: members.firstOrNull()?.avatarUrl
            } else if (members.size == 2) {
                val firstOtherMember = members.where().notEqualTo(RoomMemberSummaryEntityFields.USER_ID, userId).findFirst()
                return firstOtherMember?.avatarUrl
            }
        }

        return null
    }
}
