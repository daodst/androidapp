
package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import timber.log.Timber

internal open class EventAnnotationsSummaryEntity(
        @PrimaryKey
        var eventId: String = "",
        var roomId: String? = null,
        var reactionsSummary: RealmList<ReactionAggregatedSummaryEntity> = RealmList(),
        var editSummary: EditAggregatedSummaryEntity? = null,
        var referencesSummaryEntity: ReferencesAggregatedSummaryEntity? = null,
        var pollResponseSummary: PollResponseAggregatedSummaryEntity? = null
) : RealmObject() {

    
    fun cleanUp(originalEventSenderId: String?) {
        originalEventSenderId ?: return

        editSummary?.editions?.filter {
            it.senderId != originalEventSenderId
        }
                ?.forEach {
                    Timber.w("Deleting an edition from ${it.senderId} of event sent by $originalEventSenderId")
                    it.deleteFromRealm()
                }
    }

    companion object
}

internal fun EventAnnotationsSummaryEntity.deleteOnCascade() {
    reactionsSummary.deleteAllFromRealm()
    editSummary?.deleteFromRealm()
    referencesSummaryEntity?.deleteFromRealm()
    pollResponseSummary?.deleteFromRealm()
    deleteFromRealm()
}
