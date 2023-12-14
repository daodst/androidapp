
package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass


internal open class EditAggregatedSummaryEntity(
        
        var editions: RealmList<EditionOfEvent> = RealmList()
) : RealmObject() {

    companion object
}

@RealmClass(embedded = true)
internal open class EditionOfEvent(
        var senderId: String = "",
        var eventId: String = "",
        var content: String? = null,
        var timestamp: Long = 0,
        var isLocalEcho: Boolean = false
) : RealmObject()
