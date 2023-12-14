

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject


internal open class ReactionAggregatedSummaryEntity(
        
        var key: String = "",
        
        var count: Int = 0,
        
        var addedByMe: Boolean = false,
        
        var firstTimestamp: Long = 0,
        
        var sourceEvents: RealmList<String> = RealmList(),
        
        var sourceLocalEcho: RealmList<String> = RealmList()
) : RealmObject() {

    companion object
}
