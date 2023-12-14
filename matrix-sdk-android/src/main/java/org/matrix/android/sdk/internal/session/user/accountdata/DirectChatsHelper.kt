

package org.matrix.android.sdk.internal.session.user.accountdata

import io.realm.Realm
import io.realm.RealmConfiguration
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntity
import org.matrix.android.sdk.internal.database.query.getDirectRooms
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.session.sync.model.accountdata.DirectMessagesContent
import javax.inject.Inject

internal class DirectChatsHelper @Inject constructor(
        @SessionDatabase private val realmConfiguration: RealmConfiguration
) {

    
    fun getLocalDirectMessages(filterRoomId: String? = null): DirectMessagesContent {
        return Realm.getInstance(realmConfiguration).use { realm ->
            
            realm.refresh()
            RoomSummaryEntity.getDirectRooms(realm)
                    .asSequence()
                    .filter { it.roomId != filterRoomId && it.directUserId != null && it.membership.isActive() }
                    .groupByTo(mutableMapOf(), { it.directUserId!! }, { it.roomId })
        }
    }
}
