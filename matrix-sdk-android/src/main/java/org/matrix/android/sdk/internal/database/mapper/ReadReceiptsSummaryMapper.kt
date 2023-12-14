

package org.matrix.android.sdk.internal.database.mapper

import io.realm.Realm
import io.realm.RealmList
import org.matrix.android.sdk.api.session.room.model.ReadReceipt
import org.matrix.android.sdk.internal.database.RealmSessionProvider
import org.matrix.android.sdk.internal.database.model.ReadReceiptEntity
import org.matrix.android.sdk.internal.database.model.ReadReceiptsSummaryEntity
import org.matrix.android.sdk.internal.database.model.RoomMemberSummaryEntity
import org.matrix.android.sdk.internal.database.query.where
import javax.inject.Inject

internal class ReadReceiptsSummaryMapper @Inject constructor(
        private val realmSessionProvider: RealmSessionProvider
) {

    fun map(readReceiptsSummaryEntity: ReadReceiptsSummaryEntity?): List<ReadReceipt> {
        if (readReceiptsSummaryEntity == null) {
            return emptyList()
        }
        val readReceipts = readReceiptsSummaryEntity.readReceipts
        
        return if (readReceiptsSummaryEntity.isManaged) {
            map(readReceipts, readReceiptsSummaryEntity.realm)
        } else {
            realmSessionProvider.withRealm { realm ->
                map(readReceipts, realm)
            }
        }
    }

    private fun map(readReceipts: RealmList<ReadReceiptEntity>, realm: Realm): List<ReadReceipt> {
        return readReceipts
                .mapNotNull {
                    val roomMember = RoomMemberSummaryEntity.where(realm, roomId = it.roomId, userId = it.userId).findFirst()
                            ?: return@mapNotNull null
                    ReadReceipt(roomMember.asDomain(), it.originServerTs.toLong())
                }
    }
}
