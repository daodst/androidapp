

package org.matrix.android.sdk.internal.session.sync.handler.room

import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import io.realm.kotlin.where
import org.matrix.android.sdk.api.session.crypto.model.OlmDecryptionResult
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.api.session.events.model.getRelationContentForType
import org.matrix.android.sdk.api.session.events.model.getRootThreadEventId
import org.matrix.android.sdk.api.session.events.model.isSticker
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageFormat
import org.matrix.android.sdk.api.session.room.model.message.MessageRelationContent
import org.matrix.android.sdk.api.session.room.model.message.MessageTextContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import org.matrix.android.sdk.api.settings.LightweightSettingsStorage
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.database.mapper.ContentMapper
import org.matrix.android.sdk.internal.database.mapper.EventMapper
import org.matrix.android.sdk.internal.database.mapper.asDomain
import org.matrix.android.sdk.internal.database.mapper.toEntity
import org.matrix.android.sdk.internal.database.model.EventEntity
import org.matrix.android.sdk.internal.database.model.EventEntityFields
import org.matrix.android.sdk.internal.database.model.EventInsertType
import org.matrix.android.sdk.internal.database.query.copyToRealmOrIgnore
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.session.permalinks.PermalinkFactory
import org.matrix.android.sdk.internal.session.room.send.LocalEchoEventFactory
import org.matrix.android.sdk.internal.session.room.timeline.GetEventTask
import org.matrix.android.sdk.internal.util.awaitTransaction
import javax.inject.Inject


internal class ThreadsAwarenessHandler @Inject constructor(
        private val permalinkFactory: PermalinkFactory,
        @SessionDatabase private val monarchy: Monarchy,
        private val lightweightSettingsStorage: LightweightSettingsStorage,
        private val getEventTask: GetEventTask
) {

    
    
    
    private val cacheEventRootId = hashSetOf<String>()

    
    suspend fun fetchRootThreadEventsIfNeeded(syncResponse: SyncResponse) {
        val handlingStrategy = syncResponse.rooms?.join?.let {
            RoomSyncHandler.HandlingStrategy.JOINED(it)
        }
        if (handlingStrategy !is RoomSyncHandler.HandlingStrategy.JOINED) return
        val eventList = handlingStrategy.data
                .mapNotNull { (roomId, roomSync) ->
                    roomSync.timeline?.events?.map {
                        it.copy(roomId = roomId)
                    }
                }.flatten()

        fetchRootThreadEventsIfNeeded(eventList)
    }

    
    suspend fun fetchRootThreadEventsIfNeeded(eventList: List<Event>) {
        if (eventList.isNullOrEmpty()) return

        val threadsToFetch = emptyMap<String, String>().toMutableMap()
        Realm.getInstance(monarchy.realmConfiguration).use { realm ->
            eventList.asSequence()
                    .filter {
                        isThreadEvent(it) && it.roomId != null
                    }.mapNotNull { event ->
                        getRootThreadEventId(event)?.let {
                            Pair(it, event.roomId!!)
                        }
                    }.forEach { (rootThreadEventId, roomId) ->
                        EventEntity.where(realm, rootThreadEventId).findFirst() ?: run { threadsToFetch[rootThreadEventId] = roomId }
                    }
        }
        fetchThreadsEvents(threadsToFetch)
    }

    
    private suspend fun fetchThreadsEvents(threadsToFetch: Map<String, String>) {
        val eventEntityList = threadsToFetch.mapNotNull { (eventId, roomId) ->
            fetchEvent(eventId, roomId)?.let {
                it.toEntity(roomId, SendState.SYNCED, it.ageLocalTs)
            }
        }

        if (eventEntityList.isNullOrEmpty()) return

        
        monarchy.awaitTransaction { realm ->
            eventEntityList.forEach {
                it.copyToRealmOrIgnore(realm, EventInsertType.INCREMENTAL_SYNC)
            }
        }
    }

    
    private suspend fun fetchEvent(eventId: String, roomId: String): Event? {
        return runCatching {
            getEventTask.execute(GetEventTask.Params(roomId = roomId, eventId = eventId))
        }.fold(
                onSuccess = {
                    it
                },
                onFailure = {
                    null
                })
    }

    
    fun makeEventThreadAware(realm: Realm,
                             roomId: String?,
                             event: Event?,
                             eventEntity: EventEntity? = null): String? {
        event ?: return null
        roomId ?: return null
        if (lightweightSettingsStorage.areThreadMessagesEnabled() && !isReplyEvent(event)) return null
        handleRootThreadEventsIfNeeded(realm, roomId, eventEntity, event)
        if (!isThreadEvent(event)) return null
        val eventPayload = if (!event.isEncrypted()) {
            event.content?.toMutableMap() ?: return null
        } else {
            event.mxDecryptionResult?.payload?.toMutableMap() ?: return null
        }
        val eventBody = event.getDecryptedTextSummary() ?: return null
        val threadRelation = getRootThreadRelationContent(event)
        val eventIdToInject = getPreviousEventOrRoot(event) ?: run {
            return@makeEventThreadAware injectFallbackIndicator(event, eventBody, eventEntity, eventPayload, threadRelation)
        }
        val eventToInject = getEventFromDB(realm, eventIdToInject)
        val eventToInjectBody = eventToInject?.getDecryptedTextSummary()
        var contentForNonEncrypted: String?
        if (eventToInject != null && eventToInjectBody != null) {
            
            
            val messageTextContent = injectEvent(
                    roomId = roomId,
                    eventBody = eventBody,
                    eventToInject = eventToInject,
                    eventToInjectBody = eventToInjectBody,
                    threadRelation = threadRelation) ?: return null

            
            contentForNonEncrypted = updateEventEntity(event, eventEntity, eventPayload, messageTextContent)
        } else {
            contentForNonEncrypted = injectFallbackIndicator(event, eventBody, eventEntity, eventPayload, threadRelation)
        }

        
        eventEntity?.let {
            
            handleEventsThatRelatesTo(realm, roomId, event, eventBody, false, threadRelation)
        }
        return contentForNonEncrypted
    }

    
    private fun handleRootThreadEventsIfNeeded(
            realm: Realm,
            roomId: String,
            eventEntity: EventEntity?,
            event: Event
    ): String? {
        if (!isThreadEvent(event) && cacheEventRootId.contains(eventEntity?.eventId)) {
            eventEntity?.let {
                val eventBody = event.getDecryptedTextSummary() ?: return null
                return handleEventsThatRelatesTo(realm, roomId, event, eventBody, true, null)
            }
        }
        return null
    }

    
    private fun handleEventsThatRelatesTo(
            realm: Realm,
            roomId: String,
            event: Event,
            eventBody: String,
            isFromCache: Boolean,
            threadRelation: RelationDefaultContent?
    ): String? {
        event.eventId ?: return null
        val rootThreadEventId = if (isFromCache) event.eventId else event.getRootThreadEventId() ?: return null
        eventThatRelatesTo(realm, event.eventId, rootThreadEventId)?.forEach { eventEntityFound ->
            val newEventFound = eventEntityFound.asDomain()
            val newEventBody = newEventFound.getDecryptedTextSummary() ?: return null
            val newEventPayload = newEventFound.mxDecryptionResult?.payload?.toMutableMap() ?: return null

            val messageTextContent = injectEvent(
                    roomId = roomId,
                    eventBody = newEventBody,
                    eventToInject = event,
                    eventToInjectBody = eventBody,
                    threadRelation = threadRelation) ?: return null

            return updateEventEntity(newEventFound, eventEntityFound, newEventPayload, messageTextContent)
        }
        return null
    }

    
    private fun updateEventEntity(event: Event,
                                  eventEntity: EventEntity?,
                                  eventPayload: MutableMap<String, Any>,
                                  messageTextContent: Content): String? {
        eventPayload["content"] = messageTextContent

        if (event.isEncrypted()) {
            if (event.isSticker()) {
                eventPayload["type"] = EventType.MESSAGE
            }
            event.mxDecryptionResult = event.mxDecryptionResult?.copy(payload = eventPayload)
            eventEntity?.decryptionResultJson = event.mxDecryptionResult?.let {
                MoshiProvider.providesMoshi().adapter(OlmDecryptionResult::class.java).toJson(it)
            }
        } else {
            if (event.type == EventType.STICKER) {
                eventEntity?.type = EventType.MESSAGE
            }
            eventEntity?.content = ContentMapper.map(messageTextContent)
            return ContentMapper.map(messageTextContent)
        }
        return null
    }

    
    private fun injectEvent(roomId: String,
                            eventBody: String,
                            eventToInject: Event,
                            eventToInjectBody: String,
                            threadRelation: RelationDefaultContent?
    ): Content? {
        val eventToInjectId = eventToInject.eventId ?: return null
        val eventIdToInjectSenderId = eventToInject.senderId.orEmpty()
        val permalink = permalinkFactory.createPermalink(roomId, eventToInjectId, false)
        val userLink = permalinkFactory.createPermalink(eventIdToInjectSenderId, false) ?: ""
        val replyFormatted = LocalEchoEventFactory.REPLY_PATTERN.format(
                permalink,
                userLink,
                eventIdToInjectSenderId,
                eventToInjectBody,
                eventBody)

        return MessageTextContent(
                relatesTo = threadRelation,
                msgType = MessageType.MSGTYPE_TEXT,
                format = MessageFormat.FORMAT_MATRIX_HTML,
                body = eventBody,
                formattedBody = replyFormatted
        ).toContent()
    }

    
    private fun injectFallbackIndicator(event: Event,
                                        eventBody: String,
                                        eventEntity: EventEntity?,
                                        eventPayload: MutableMap<String, Any>,
                                        threadRelation: RelationDefaultContent?): String? {
        val replyFormatted = LocalEchoEventFactory.QUOTE_PATTERN.format(
                "In reply to a thread",
                eventBody)

        val messageTextContent = MessageTextContent(
                relatesTo = threadRelation,
                msgType = MessageType.MSGTYPE_TEXT,
                format = MessageFormat.FORMAT_MATRIX_HTML,
                body = eventBody,
                formattedBody = replyFormatted
        ).toContent()

        return updateEventEntity(event, eventEntity, eventPayload, messageTextContent)
    }

    private fun eventThatRelatesTo(realm: Realm, currentEventId: String, rootThreadEventId: String): List<EventEntity>? {
        val threadList = realm.where<EventEntity>()
                .beginGroup()
                .equalTo(EventEntityFields.ROOT_THREAD_EVENT_ID, rootThreadEventId)
                .or()
                .equalTo(EventEntityFields.EVENT_ID, rootThreadEventId)
                .endGroup()
                .and()
                .findAll()
        cacheEventRootId.add(rootThreadEventId)
        return threadList.filter {
            it.asDomain().getRelationContentForType(RelationType.THREAD)?.inReplyTo?.eventId == currentEventId
        }
    }

    
    private fun getEventFromDB(realm: Realm, eventId: String): Event? {
        val eventEntity = EventEntity.where(realm, eventId = eventId).findFirst() ?: return null
        return EventMapper.map(eventEntity)
    }

    
    private fun isThreadEvent(event: Event): Boolean =
            event.content.toModel<MessageRelationContent>()?.relatesTo?.type == RelationType.THREAD

    
    private fun getRootThreadEventId(event: Event): String? =
            event.content.toModel<MessageRelationContent>()?.relatesTo?.eventId

    private fun getRootThreadRelationContent(event: Event): RelationDefaultContent? =
            event.content.toModel<MessageRelationContent>()?.relatesTo

    private fun getPreviousEventOrRoot(event: Event): String? =
            event.content.toModel<MessageRelationContent>()?.relatesTo?.inReplyTo?.eventId

    
    private fun isReplyEvent(event: Event): Boolean {
        return isThreadEvent(event) && !isFallingBack(event) && getPreviousEventOrRoot(event) != null
    }

    private fun isFallingBack(event: Event): Boolean =
            event.content.toModel<MessageRelationContent>()?.relatesTo?.isFallingBack == true

    @Suppress("UNCHECKED_CAST")
    private fun getValueFromPayload(payload: JsonDict?, key: String): String? {
        val content = payload?.get("content") as? JsonDict
        return content?.get(key) as? String
    }
}
