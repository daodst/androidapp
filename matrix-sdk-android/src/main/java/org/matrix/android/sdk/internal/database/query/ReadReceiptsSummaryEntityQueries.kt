

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.ReadReceiptsSummaryEntity
import org.matrix.android.sdk.internal.database.model.ReadReceiptsSummaryEntityFields

internal fun ReadReceiptsSummaryEntity.Companion.where(realm: Realm, eventId: String): RealmQuery<ReadReceiptsSummaryEntity> {
    return realm.where<ReadReceiptsSummaryEntity>()
            .equalTo(ReadReceiptsSummaryEntityFields.EVENT_ID, eventId)
}

internal fun ReadReceiptsSummaryEntity.Companion.whereInRoom(realm: Realm, roomId: String): RealmQuery<ReadReceiptsSummaryEntity> {
    return realm.where<ReadReceiptsSummaryEntity>()
            .equalTo(ReadReceiptsSummaryEntityFields.ROOM_ID, roomId)
}
