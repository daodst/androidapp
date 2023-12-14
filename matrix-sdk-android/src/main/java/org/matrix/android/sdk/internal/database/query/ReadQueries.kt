
package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmConfiguration
import org.matrix.android.sdk.api.session.events.model.LocalEcho
import org.matrix.android.sdk.internal.database.helper.isMoreRecentThan
import org.matrix.android.sdk.internal.database.model.ChunkEntity
import org.matrix.android.sdk.internal.database.model.ReadMarkerEntity
import org.matrix.android.sdk.internal.database.model.ReadReceiptEntity
import org.matrix.android.sdk.internal.database.model.TimelineEventEntity

internal fun isEventRead(realmConfiguration: RealmConfiguration,
                         userId: String?,
                         roomId: String?,
                         eventId: String?): Boolean {
    if (userId.isNullOrBlank() || roomId.isNullOrBlank() || eventId.isNullOrBlank()) {
        return false
    }
    if (LocalEcho.isLocalEchoId(eventId)) {
        return true
    }

    return Realm.getInstance(realmConfiguration).use { realm ->
        val eventToCheck = TimelineEventEntity.where(realm, roomId, eventId).findFirst()
        when {
            
            eventToCheck == null                                          -> false
            eventToCheck.root?.sender == userId                           -> true
            
            latestEventIsFromSelf(realm, roomId, userId)                  -> true
            eventToCheck.isBeforeLatestReadReceipt(realm, roomId, userId) -> true
            else                                                          -> false
        }
    }
}

private fun latestEventIsFromSelf(realm: Realm, roomId: String, userId: String) = TimelineEventEntity.latestEvent(realm, roomId, true)
        ?.root?.sender == userId

private fun TimelineEventEntity.isBeforeLatestReadReceipt(realm: Realm, roomId: String, userId: String): Boolean {
    return ReadReceiptEntity.where(realm, roomId, userId).findFirst()?.let { readReceipt ->
        val readReceiptEvent = TimelineEventEntity.where(realm, roomId, readReceipt.eventId).findFirst()
        readReceiptEvent?.isMoreRecentThan(this)
    } ?: false
}


private fun hasReadMissingEvent(realm: Realm, latestChunkEntity: ChunkEntity, roomId: String, userId: String, eventId: String): Boolean {
    return realm.doesEventExistInChunkHistory(eventId) && realm.hasReadReceiptInLatestChunk(latestChunkEntity, roomId, userId)
}

private fun Realm.doesEventExistInChunkHistory(eventId: String): Boolean {
    return ChunkEntity.findIncludingEvent(this, eventId) != null
}

private fun Realm.hasReadReceiptInLatestChunk(latestChunkEntity: ChunkEntity, roomId: String, userId: String): Boolean {
    return ReadReceiptEntity.where(this, roomId = roomId, userId = userId).findFirst()?.let {
        latestChunkEntity.timelineEvents.find(it.eventId)
    } != null
}

internal fun isReadMarkerMoreRecent(realmConfiguration: RealmConfiguration,
                                    roomId: String?,
                                    eventId: String?): Boolean {
    if (roomId.isNullOrBlank() || eventId.isNullOrBlank()) {
        return false
    }
    return Realm.getInstance(realmConfiguration).use { realm ->
        val eventToCheck = TimelineEventEntity.where(realm, roomId = roomId, eventId = eventId).findFirst()
        val eventToCheckChunk = eventToCheck?.chunk?.firstOrNull()
        val readMarker = ReadMarkerEntity.where(realm, roomId).findFirst() ?: return false
        val readMarkerEvent = TimelineEventEntity.where(realm, roomId = roomId, eventId = readMarker.eventId).findFirst()
        val readMarkerChunk = readMarkerEvent?.chunk?.firstOrNull()
        if (eventToCheckChunk == readMarkerChunk) {
            val readMarkerIndex = readMarkerEvent?.displayIndex ?: Int.MIN_VALUE
            val eventToCheckIndex = eventToCheck?.displayIndex ?: Int.MAX_VALUE
            eventToCheckIndex <= readMarkerIndex
        } else {
            eventToCheckChunk != null && readMarkerChunk?.isMoreRecentThan(eventToCheckChunk) == true
        }
    }
}
