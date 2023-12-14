

package org.matrix.android.sdk.internal.crypto

import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.internal.database.model.EventEntity
import org.matrix.android.sdk.internal.database.model.EventEntityFields
import org.matrix.android.sdk.internal.database.query.whereType
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.session.room.membership.RoomMemberHelper
import org.matrix.android.sdk.internal.util.fetchCopied
import javax.inject.Inject


internal class CryptoSessionInfoProvider @Inject constructor(
        @SessionDatabase private val monarchy: Monarchy
) {

    fun isRoomEncrypted(roomId: String): Boolean {
        
        
        val encryptionEvent = monarchy.fetchCopied { realm ->
            EventEntity.whereType(realm, roomId = roomId, type = EventType.STATE_ROOM_ENCRYPTION)
                    .isEmpty(EventEntityFields.STATE_KEY)
                    .findFirst()
        }
        return encryptionEvent != null
    }

    
    fun getRoomUserIds(roomId: String, allActive: Boolean): List<String> {
        var userIds: List<String> = emptyList()
        monarchy.doWithRealm { realm ->
            userIds = if (allActive) {
                RoomMemberHelper(realm, roomId).getActiveRoomMemberIds()
            } else {
                RoomMemberHelper(realm, roomId).getJoinedRoomMemberIds()
            }
        }
        return userIds
    }
}
