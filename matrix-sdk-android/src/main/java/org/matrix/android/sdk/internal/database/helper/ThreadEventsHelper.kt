

package org.matrix.android.sdk.internal.database.helper

import com.squareup.moshi.JsonDataException
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.Sort
import org.matrix.android.sdk.api.session.events.model.UnsignedData
import org.matrix.android.sdk.api.session.events.model.isRedacted
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.threads.ThreadNotificationState
import org.matrix.android.sdk.internal.database.mapper.asDomain
import org.matrix.android.sdk.internal.database.model.ChunkEntity
import org.matrix.android.sdk.internal.database.model.EventAnnotationsSummaryEntity
import org.matrix.android.sdk.internal.database.model.EventEntity
import org.matrix.android.sdk.internal.database.model.ReadReceiptEntity
import org.matrix.android.sdk.internal.database.model.TimelineEventEntity
import org.matrix.android.sdk.internal.database.model.TimelineEventEntityFields
import org.matrix.android.sdk.internal.database.query.find
import org.matrix.android.sdk.internal.database.query.findIncludingEvent
import org.matrix.android.sdk.internal.database.query.findLastForwardChunkOfRoom
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.database.query.whereRoomId
import org.matrix.android.sdk.internal.di.MoshiProvider
import timber.log.Timber

private typealias Summary = Pair<Int, TimelineEventEntity>?


internal fun Map<String, EventEntity>.updateThreadSummaryIfNeeded(
        roomId: String,
        realm: Realm, currentUserId: String,
        chunkEntity: ChunkEntity? = null,
        shouldUpdateNotifications: Boolean = true) {
    for ((rootThreadEventId, eventEntity) in this) {
        eventEntity.threadSummaryInThread(eventEntity.realm, rootThreadEventId, chunkEntity)?.let { threadSummary ->

            val inThreadMessages = threadSummary.first
            val latestEventInThread = threadSummary.second

            
            val rootThreadEvent = if (eventEntity.isThread()) eventEntity.findRootThreadEvent() else eventEntity

            rootThreadEvent?.markEventAsRoot(
                    inThreadMessages = inThreadMessages,
                    latestMessageTimelineEventEntity = latestEventInThread
            )
        }
    }

    if (shouldUpdateNotifications) {
        updateNotificationsNew(roomId, realm, currentUserId)
    }
}


internal fun EventEntity.findRootThreadEvent(): EventEntity? =
        rootThreadEventId?.let {
            EventEntity
                    .where(realm, it)
                    .findFirst()
        }


internal fun EventEntity.markEventAsRoot(
        inThreadMessages: Int,
        latestMessageTimelineEventEntity: TimelineEventEntity?) {
    isRootThread = true
    numberOfThreads = inThreadMessages
    threadSummaryLatestMessage = latestMessageTimelineEventEntity
}


internal fun EventEntity.threadSummaryInThread(realm: Realm, rootThreadEventId: String, chunkEntity: ChunkEntity?): Summary {
    val inThreadMessages = countInThreadMessages(
            realm = realm,
            roomId = roomId,
            rootThreadEventId = rootThreadEventId
    )

    if (inThreadMessages <= 0) return null

    
    var chunk = ChunkEntity.findLastForwardChunkOfRoom(realm, roomId) ?: chunkEntity ?: return null
    var result: TimelineEventEntity? = null

    
    while (result == null) {
        result = findLatestSortedChunkEvent(chunk, rootThreadEventId)
        chunk = ChunkEntity.find(realm, roomId, nextToken = chunk.prevToken) ?: break
    }

    if (result == null && chunkEntity != null) {
        
        result = findLatestSortedChunkEvent(chunkEntity, rootThreadEventId)
    } else if (result != null && chunkEntity != null) {
        val currentChunkLatestEvent = findLatestSortedChunkEvent(chunkEntity, rootThreadEventId)
        result = findMostRecentEvent(result, currentChunkLatestEvent)
    }

    result ?: return null

    return Summary(inThreadMessages, result)
}


internal fun countInThreadMessages(realm: Realm, roomId: String, rootThreadEventId: String): Int =
        TimelineEventEntity
                .whereRoomId(realm, roomId = roomId)
                .equalTo(TimelineEventEntityFields.ROOT.ROOT_THREAD_EVENT_ID, rootThreadEventId)
                .distinct(TimelineEventEntityFields.ROOT.EVENT_ID)
                .findAll()
                .filterNot { timelineEvent ->
                    timelineEvent.root
                            ?.unsignedData
                            ?.takeIf { it.isNotBlank() }
                            ?.toUnsignedData()
                            .isRedacted()
                }.size


private fun String.toUnsignedData(): UnsignedData? =
        try {
            MoshiProvider.providesMoshi().adapter(UnsignedData::class.java).fromJson(this)
        } catch (ex: JsonDataException) {
            Timber.e(ex, "Failed to parse UnsignedData")
            null
        }


private fun findMostRecentEvent(result: TimelineEventEntity, currentChunkLatestEvent: TimelineEventEntity?): TimelineEventEntity {
    currentChunkLatestEvent ?: return result
    val currentChunkEventTimestamp = currentChunkLatestEvent.root?.originServerTs ?: return result
    val resultTimestamp = result.root?.originServerTs ?: return result
    if (currentChunkEventTimestamp > resultTimestamp) {
        return currentChunkLatestEvent
    }
    return result
}


private fun findLatestSortedChunkEvent(chunk: ChunkEntity, rootThreadEventId: String): TimelineEventEntity? =
        chunk.timelineEvents.sort(TimelineEventEntityFields.DISPLAY_INDEX, Sort.DESCENDING)?.firstOrNull {
            it.root?.rootThreadEventId == rootThreadEventId
        }


internal fun TimelineEventEntity.Companion.findAllThreadsForRoomId(realm: Realm, roomId: String): RealmQuery<TimelineEventEntity> =
        TimelineEventEntity
                .whereRoomId(realm, roomId = roomId)
                .equalTo(TimelineEventEntityFields.ROOT.IS_ROOT_THREAD, true)
                .equalTo(TimelineEventEntityFields.OWNED_BY_THREAD_CHUNK, false)
                .sort("${TimelineEventEntityFields.ROOT.THREAD_SUMMARY_LATEST_MESSAGE}.${TimelineEventEntityFields.ROOT.ORIGIN_SERVER_TS}", Sort.DESCENDING)


internal fun List<TimelineEvent>.mapEventsWithEdition(realm: Realm, roomId: String): List<TimelineEvent> =
        this.map {
            EventAnnotationsSummaryEntity
                    .where(realm, roomId, eventId = it.eventId)
                    .findFirst()
                    ?.editSummary
                    ?.editions
                    ?.lastOrNull()
                    ?.eventId
                    ?.let { editedEventId ->
                        TimelineEventEntity.where(realm, roomId, eventId = editedEventId).findFirst()?.let { editedEvent ->
                            it.root.threadDetails = it.root.threadDetails?.copy(lastRootThreadEdition = editedEvent.root?.asDomain()?.getDecryptedTextSummary()
                                    ?: "(edited)")
                            it
                        } ?: it
                    } ?: it
        }


internal fun TimelineEventEntity.Companion.findAllLocalThreadNotificationsForRoomId(realm: Realm, roomId: String): RealmQuery<TimelineEventEntity> =
        TimelineEventEntity
                .whereRoomId(realm, roomId = roomId)
                .equalTo(TimelineEventEntityFields.ROOT.IS_ROOT_THREAD, true)
                .beginGroup()
                .equalTo(TimelineEventEntityFields.ROOT.THREAD_NOTIFICATION_STATE_STR, ThreadNotificationState.NEW_MESSAGE.name)
                .or()
                .equalTo(TimelineEventEntityFields.ROOT.THREAD_NOTIFICATION_STATE_STR, ThreadNotificationState.NEW_HIGHLIGHTED_MESSAGE.name)
                .endGroup()


internal fun TimelineEventEntity.Companion.isUserParticipatingInThread(realm: Realm, roomId: String, rootThreadEventId: String, senderId: String): Boolean =
        TimelineEventEntity
                .whereRoomId(realm, roomId = roomId)
                .equalTo(TimelineEventEntityFields.ROOT.ROOT_THREAD_EVENT_ID, rootThreadEventId)
                .equalTo(TimelineEventEntityFields.ROOT.SENDER, senderId)
                .findFirst()
                ?.let { true }
                ?: false


internal fun TimelineEventEntity.Companion.isUserMentionedInThread(realm: Realm, roomId: String, rootThreadEventId: String, userId: String): Boolean =
        TimelineEventEntity
                .whereRoomId(realm, roomId = roomId)
                .equalTo(TimelineEventEntityFields.ROOT.ROOT_THREAD_EVENT_ID, rootThreadEventId)
                .equalTo(TimelineEventEntityFields.ROOT.SENDER, userId)
                .findAll()
                .firstOrNull { isUserMentioned(userId, it) }
                ?.let { true }
                ?: false


internal fun findMyReadReceipt(realm: Realm, roomId: String, userId: String): String? =
        ReadReceiptEntity.where(realm, roomId = roomId, userId = userId)
                .findFirst()
                ?.eventId


internal fun isUserMentioned(currentUserId: String, timelineEventEntity: TimelineEventEntity?): Boolean {
    return timelineEventEntity?.root?.asDomain()?.isUserMentioned(currentUserId) == true
}


internal fun updateNotificationsNew(roomId: String, realm: Realm, currentUserId: String) {
    val readReceipt = findMyReadReceipt(realm, roomId, currentUserId) ?: return

    val readReceiptChunk = ChunkEntity
            .findIncludingEvent(realm, readReceipt) ?: return

    val readReceiptChunkTimelineEvents = readReceiptChunk
            .timelineEvents
            .where()
            .equalTo(TimelineEventEntityFields.ROOM_ID, roomId)
            .sort(TimelineEventEntityFields.DISPLAY_INDEX, Sort.ASCENDING)
            .findAll() ?: return

    val readReceiptChunkPosition = readReceiptChunkTimelineEvents.indexOfFirst { it.eventId == readReceipt }

    if (readReceiptChunkPosition == -1) return

    if (readReceiptChunkPosition < readReceiptChunkTimelineEvents.lastIndex) {
        

        val threadEventsAfterReadReceipt = readReceiptChunkTimelineEvents
                .slice(readReceiptChunkPosition..readReceiptChunkTimelineEvents.lastIndex)
                .filter { it.root?.isThread() == true }

        
        
        

        
        val userMentionsList = threadEventsAfterReadReceipt
                .filter {
                    isUserMentioned(currentUserId = currentUserId, it)
                }.map {
                    it.root?.rootThreadEventId
                }

        
        val rootThreads = threadEventsAfterReadReceipt.distinctBy { it.root?.rootThreadEventId }.mapNotNull { it.root?.rootThreadEventId }

        
        rootThreads.forEach { eventId ->
            val isUserParticipating = TimelineEventEntity.isUserParticipatingInThread(
                    realm = realm,
                    roomId = roomId,
                    rootThreadEventId = eventId,
                    senderId = currentUserId)
            val rootThreadEventEntity = EventEntity.where(realm, eventId).findFirst()

            if (isUserParticipating) {
                rootThreadEventEntity?.threadNotificationState = ThreadNotificationState.NEW_MESSAGE
            }

            if (userMentionsList.contains(eventId)) {
                rootThreadEventEntity?.threadNotificationState = ThreadNotificationState.NEW_HIGHLIGHTED_MESSAGE
            }
        }
    }
}
