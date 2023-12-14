
package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject


internal open class PollResponseAggregatedSummaryEntity(
        
        
        var aggregatedContent: String? = null,

        
        var closedTime: Long? = null,
        
        var nbOptions: Int = 0,

        
        var sourceEvents: RealmList<String> = RealmList(),
        var sourceLocalEchoEvents: RealmList<String> = RealmList()
) : RealmObject() {

    companion object
}
