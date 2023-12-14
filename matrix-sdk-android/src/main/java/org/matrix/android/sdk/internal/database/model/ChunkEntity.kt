

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.Index
import io.realm.annotations.LinkingObjects
import org.matrix.android.sdk.internal.extensions.assertIsManaged
import org.matrix.android.sdk.internal.extensions.clearWith

internal open class ChunkEntity(
        @Index var prevToken: String? = null,
        
        @Index var nextToken: String? = null,
        var prevChunk: ChunkEntity? = null,
        var nextChunk: ChunkEntity? = null,
        var stateEvents: RealmList<EventEntity> = RealmList(),
        var timelineEvents: RealmList<TimelineEventEntity> = RealmList(),
        
        @Index var isLastForward: Boolean = false,
        @Index var isLastBackward: Boolean = false,
        
        @Index var rootThreadEventId: String? = null,
        @Index var isLastForwardThread: Boolean = false,
) : RealmObject() {

    fun identifier() = "${prevToken}_$nextToken"

    
    fun hasBeenALastForwardChunk() = nextToken == null && !isLastForward

    @LinkingObjects("chunks")
    val room: RealmResults<RoomEntity>? = null

    companion object
}

internal fun ChunkEntity.deleteOnCascade(
        deleteStateEvents: Boolean,
        canDeleteRoot: Boolean) {
    assertIsManaged()
    if (deleteStateEvents) {
        stateEvents.deleteAllFromRealm()
    }
    timelineEvents.clearWith {
        val deleteRoot = canDeleteRoot && (it.root?.stateKey == null || deleteStateEvents)
        if (deleteRoot) {
            room?.firstOrNull()?.removeThreadSummaryIfNeeded(it.eventId)
        }
        it.deleteOnCascade(deleteRoot)
    }
    deleteFromRealm()
}


internal fun ChunkEntity.deleteAndClearThreadEvents() {
    assertIsManaged()
    timelineEvents
            .filter { it.ownedByThreadChunk }
            .forEach {
                it.deleteOnCascade(false)
            }
    deleteFromRealm()
}
