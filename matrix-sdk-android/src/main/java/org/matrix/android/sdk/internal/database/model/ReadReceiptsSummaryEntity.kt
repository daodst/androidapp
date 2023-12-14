

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey

internal open class ReadReceiptsSummaryEntity(
        @PrimaryKey
        var eventId: String = "",
        var roomId: String = "",
        var readReceipts: RealmList<ReadReceiptEntity> = RealmList()
) : RealmObject() {

    @LinkingObjects("readReceipts")
    val timelineEvent: RealmResults<TimelineEventEntity>? = null

    companion object
}

internal fun ReadReceiptsSummaryEntity.deleteOnCascade() {
    readReceipts.deleteAllFromRealm()
    deleteFromRealm()
}
