
package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject

internal open class ReferencesAggregatedSummaryEntity(
        var eventId: String = "",
        var content: String? = null,
        
        var sourceEvents: RealmList<String> = RealmList(),
        
        var sourceLocalEcho: RealmList<String> = RealmList()
) : RealmObject() {

    companion object
}
