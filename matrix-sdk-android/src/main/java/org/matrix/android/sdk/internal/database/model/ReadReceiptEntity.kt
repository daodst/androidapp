

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey

internal open class ReadReceiptEntity(@PrimaryKey var primaryKey: String = "",
                                      var eventId: String = "",
                                      var roomId: String = "",
                                      var userId: String = "",
                                      var originServerTs: Double = 0.0
) : RealmObject() {
    companion object

    @LinkingObjects("readReceipts")
    val summary: RealmResults<ReadReceiptsSummaryEntity>? = null
}
