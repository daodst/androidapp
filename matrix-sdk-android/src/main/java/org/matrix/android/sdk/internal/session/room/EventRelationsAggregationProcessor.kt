
package org.matrix.android.sdk.internal.session.room

import io.realm.Realm
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.crypto.verification.VerificationState
import org.matrix.android.sdk.api.session.events.model.AggregatedAnnotation
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.LocalEcho
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.api.session.events.model.content.EncryptedEventContent
import org.matrix.android.sdk.api.session.events.model.getRelationContent
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.PollSummaryContent
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.ReferencesAggregatedContent
import org.matrix.android.sdk.api.session.room.model.VoteInfo
import org.matrix.android.sdk.api.session.room.model.VoteSummary
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageEndPollContent
import org.matrix.android.sdk.api.session.room.model.message.MessageLiveLocationContent
import org.matrix.android.sdk.api.session.room.model.message.MessagePollContent
import org.matrix.android.sdk.api.session.room.model.message.MessagePollResponseContent
import org.matrix.android.sdk.api.session.room.model.message.MessageRelationContent
import org.matrix.android.sdk.api.session.room.model.relation.ReactionContent
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.crypto.verification.toState
import org.matrix.android.sdk.internal.database.helper.findRootThreadEvent
import org.matrix.android.sdk.internal.database.mapper.ContentMapper
import org.matrix.android.sdk.internal.database.mapper.EventMapper
import org.matrix.android.sdk.internal.database.model.EditAggregatedSummaryEntity
import org.matrix.android.sdk.internal.database.model.EditionOfEvent
import org.matrix.android.sdk.internal.database.model.EventAnnotationsSummaryEntity
import org.matrix.android.sdk.internal.database.model.EventEntity
import org.matrix.android.sdk.internal.database.model.EventInsertType
import org.matrix.android.sdk.internal.database.model.PollResponseAggregatedSummaryEntity
import org.matrix.android.sdk.internal.database.model.ReactionAggregatedSummaryEntity
import org.matrix.android.sdk.internal.database.model.ReactionAggregatedSummaryEntityFields
import org.matrix.android.sdk.internal.database.model.ReferencesAggregatedSummaryEntity
import org.matrix.android.sdk.internal.database.model.TimelineEventEntity
import org.matrix.android.sdk.internal.database.model.TimelineEventEntityFields
import org.matrix.android.sdk.internal.database.query.create
import org.matrix.android.sdk.internal.database.query.getOrCreate
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionId
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.session.EventInsertLiveProcessor
import org.matrix.android.sdk.internal.session.room.aggregation.livelocation.LiveLocationAggregationProcessor
import org.matrix.android.sdk.internal.session.room.state.StateEventDataSource
import timber.log.Timber
import javax.inject.Inject

internal class EventRelationsAggregationProcessor @Inject constructor(
        @UserId private val userId: String,
        private val stateEventDataSource: StateEventDataSource,
        @SessionId private val sessionId: String,
        private val sessionManager: SessionManager,
        private val liveLocationAggregationProcessor: LiveLocationAggregationProcessor
) : EventInsertLiveProcessor {

    private val allowedTypes = listOf(
            EventType.MESSAGE,
            EventType.REDACTION,
            EventType.REACTION,
            EventType.KEY_VERIFICATION_DONE,
            EventType.KEY_VERIFICATION_CANCEL,
            EventType.KEY_VERIFICATION_ACCEPT,
            EventType.KEY_VERIFICATION_START,
            EventType.KEY_VERIFICATION_MAC,
            
            
            EventType.KEY_VERIFICATION_KEY,
            EventType.ENCRYPTED
    ) + EventType.POLL_START + EventType.POLL_RESPONSE + EventType.POLL_END + EventType.BEACON_LOCATION_DATA

    override fun shouldProcess(eventId: String, eventType: String, insertType: EventInsertType): Boolean {
        return allowedTypes.contains(eventType)
    }

    override suspend fun process(realm: Realm, event: Event) {
        try { 
            val roomId = event.roomId
            if (roomId == null) {
                Timber.w("Event has no room id ${event.eventId}")
                return
            }
            val isLocalEcho = LocalEcho.isLocalEchoId(event.eventId ?: "")
            when (event.type) {
                EventType.REACTION                -> {
                    
                    Timber.v("###REACTION in room $roomId , reaction eventID ${event.eventId}")
                    handleReaction(realm, event, roomId, isLocalEcho)
                }
                EventType.MESSAGE                 -> {
                    if (event.unsignedData?.relations?.annotations != null) {
                        Timber.v("###REACTION Aggregation in room $roomId for event ${event.eventId}")
                        handleInitialAggregatedRelations(realm, event, roomId, event.unsignedData.relations.annotations)

                        EventAnnotationsSummaryEntity.where(realm, roomId, event.eventId ?: "").findFirst()
                                ?.let {
                                    TimelineEventEntity.where(realm, roomId = roomId, eventId = event.eventId ?: "").findAll()
                                            ?.forEach { tet -> tet.annotations = it }
                                }
                    }

                    val content: MessageContent? = event.content.toModel()
                    if (content?.relatesTo?.type == RelationType.REPLACE) {
                        Timber.v("###REPLACE in room $roomId for event ${event.eventId}")
                        
                        handleReplace(realm, event, content, roomId, isLocalEcho)
                    }
                }

                EventType.KEY_VERIFICATION_DONE,
                EventType.KEY_VERIFICATION_CANCEL,
                EventType.KEY_VERIFICATION_ACCEPT,
                EventType.KEY_VERIFICATION_START,
                EventType.KEY_VERIFICATION_MAC,
                EventType.KEY_VERIFICATION_READY,
                EventType.KEY_VERIFICATION_KEY    -> {
                    Timber.v("## SAS REF in room $roomId for event ${event.eventId}")
                    event.content.toModel<MessageRelationContent>()?.relatesTo?.let {
                        if (it.type == RelationType.REFERENCE && it.eventId != null) {
                            handleVerification(realm, event, roomId, isLocalEcho, it.eventId)
                        }
                    }
                }

                EventType.ENCRYPTED               -> {
                    
                    val encryptedEventContent = event.content.toModel<EncryptedEventContent>()
                    if (encryptedEventContent?.relatesTo?.type == RelationType.REPLACE ||
                            encryptedEventContent?.relatesTo?.type == RelationType.RESPONSE
                    ) {
                        event.getClearContent().toModel<MessageContent>()?.let {
                            if (encryptedEventContent.relatesTo.type == RelationType.REPLACE) {
                                Timber.v("###REPLACE in room $roomId for event ${event.eventId}")
                                
                                handleReplace(realm, event, it, roomId, isLocalEcho, encryptedEventContent.relatesTo.eventId)
                            } else if (event.getClearType() in EventType.POLL_RESPONSE) {
                                event.getClearContent().toModel<MessagePollResponseContent>(catchError = true)?.let { pollResponseContent ->
                                    Timber.v("###RESPONSE in room $roomId for event ${event.eventId}")
                                    handleResponse(realm, event, pollResponseContent, roomId, isLocalEcho, encryptedEventContent.relatesTo.eventId)
                                }
                            }
                        }
                    } else if (encryptedEventContent?.relatesTo?.type == RelationType.REFERENCE) {
                        when (event.getClearType()) {
                            EventType.KEY_VERIFICATION_DONE,
                            EventType.KEY_VERIFICATION_CANCEL,
                            EventType.KEY_VERIFICATION_ACCEPT,
                            EventType.KEY_VERIFICATION_START,
                            EventType.KEY_VERIFICATION_MAC,
                            EventType.KEY_VERIFICATION_READY,
                            EventType.KEY_VERIFICATION_KEY    -> {
                                Timber.v("## SAS REF in room $roomId for event ${event.eventId}")
                                encryptedEventContent.relatesTo.eventId?.let {
                                    handleVerification(realm, event, roomId, isLocalEcho, it)
                                }
                            }
                            in EventType.POLL_RESPONSE        -> {
                                event.getClearContent().toModel<MessagePollResponseContent>(catchError = true)?.let {
                                    handleResponse(realm, event, it, roomId, isLocalEcho, event.getRelationContent()?.eventId)
                                }
                            }
                            in EventType.POLL_END             -> {
                                event.content.toModel<MessageEndPollContent>(catchError = true)?.let {
                                    handleEndPoll(realm, event, it, roomId, isLocalEcho)
                                }
                            }
                            in EventType.BEACON_LOCATION_DATA -> {
                                event.content.toModel<MessageLiveLocationContent>(catchError = true)?.let {
                                    liveLocationAggregationProcessor.handleLiveLocation(realm, event, it, roomId, isLocalEcho)
                                }
                            }
                        }
                    } else if (encryptedEventContent?.relatesTo?.type == RelationType.ANNOTATION) {
                        
                        if (event.getClearType() == EventType.REACTION) {
                            
                            Timber.v("###REACTION e2e in room $roomId , reaction eventID ${event.eventId}")
                            handleReaction(realm, event, roomId, isLocalEcho)
                        }
                    }
                    
                }
                EventType.REDACTION               -> {
                    val eventToPrune = event.redacts?.let { EventEntity.where(realm, eventId = it).findFirst() }
                            ?: return
                    when (eventToPrune.type) {
                        EventType.MESSAGE  -> {
                            Timber.d("REDACTION for message ${eventToPrune.eventId}")

                            
                            val contentModel = ContentMapper.map(eventToPrune.content)?.toModel<MessageContent>()
                            if (RelationType.REPLACE == contentModel?.relatesTo?.type && contentModel.relatesTo?.eventId != null) {
                                handleRedactionOfReplace(realm, eventToPrune, contentModel.relatesTo!!.eventId!!)
                            }
                        }
                        EventType.REACTION -> {
                            handleReactionRedact(realm, eventToPrune)
                        }
                    }
                }
                in EventType.POLL_START           -> {
                    val content: MessagePollContent? = event.content.toModel()
                    if (content?.relatesTo?.type == RelationType.REPLACE) {
                        Timber.v("###REPLACE in room $roomId for event ${event.eventId}")
                        
                        handleReplace(realm, event, content, roomId, isLocalEcho)
                    }
                }
                in EventType.POLL_RESPONSE        -> {
                    event.content.toModel<MessagePollResponseContent>(catchError = true)?.let {
                        handleResponse(realm, event, it, roomId, isLocalEcho)
                    }
                }
                in EventType.POLL_END             -> {
                    event.content.toModel<MessageEndPollContent>(catchError = true)?.let {
                        handleEndPoll(realm, event, it, roomId, isLocalEcho)
                    }
                }
                in EventType.BEACON_LOCATION_DATA -> {
                    event.content.toModel<MessageLiveLocationContent>(catchError = true)?.let {
                        liveLocationAggregationProcessor.handleLiveLocation(realm, event, it, roomId, isLocalEcho)
                    }
                }
                else                              -> Timber.v("UnHandled event ${event.eventId}")
            }
        } catch (t: Throwable) {
            Timber.e(t, "## Should not happen ")
        }
    }

    
    private val SHOULD_HANDLE_SERVER_AGREGGATION = false 

    private fun handleReplace(realm: Realm,
                              event: Event,
                              content: MessageContent,
                              roomId: String,
                              isLocalEcho: Boolean,
                              relatedEventId: String? = null) {
        val eventId = event.eventId ?: return
        val targetEventId = relatedEventId ?: content.relatesTo?.eventId ?: return
        val newContent = content.newContent ?: return

        
        val editedEvent = EventEntity.where(realm, targetEventId).findFirst()
        if (editedEvent == null) {
            
        } else if (editedEvent.sender != event.senderId) {
            
            Timber.w("Ignore edition by someone else")
            return
        }

        
        val eventAnnotationsSummaryEntity = EventAnnotationsSummaryEntity.getOrCreate(realm, roomId, targetEventId)

        
        val existingSummary = eventAnnotationsSummaryEntity.editSummary
        if (existingSummary == null) {
            Timber.v("###REPLACE new edit summary for $targetEventId, creating one (localEcho:$isLocalEcho)")
            
            eventAnnotationsSummaryEntity.editSummary = realm.createObject(EditAggregatedSummaryEntity::class.java)
                    .also { editSummary ->
                        editSummary.editions.add(
                                EditionOfEvent(
                                        senderId = event.senderId ?: "",
                                        eventId = event.eventId,
                                        content = ContentMapper.map(newContent),
                                        timestamp = if (isLocalEcho) 0 else event.originServerTs ?: 0,
                                        isLocalEcho = isLocalEcho
                                )
                        )
                    }
        } else {
            if (existingSummary.editions.any { it.eventId == eventId }) {
                
                Timber.v("###REPLACE ignoring event for summary, it's known $eventId")
                return
            }

            ContentMapper
                    .map(eventAnnotationsSummaryEntity.pollResponseSummary?.aggregatedContent)
                    ?.toModel<PollSummaryContent>()
                    ?.let { existingPollSummaryContent ->
                        eventAnnotationsSummaryEntity.pollResponseSummary?.aggregatedContent = ContentMapper.map(
                                PollSummaryContent(
                                        myVote = existingPollSummaryContent.myVote,
                                        votes = emptyList(),
                                        votesSummary = emptyMap(),
                                        totalVotes = 0,
                                        winnerVoteCount = 0,
                                )
                                        .toContent())
                    }

            val txId = event.unsignedData?.transactionId
            
            if (!isLocalEcho && existingSummary.editions.any { it.eventId == txId }) {
                
                Timber.v("###REPLACE Receiving remote echo of edit (edit already done)")
                existingSummary.editions.firstOrNull { it.eventId == txId }?.let {
                    it.eventId = event.eventId
                    it.timestamp = event.originServerTs ?: System.currentTimeMillis()
                    it.isLocalEcho = false
                }
            } else {
                Timber.v("###REPLACE Computing aggregated edit summary (isLocalEcho:$isLocalEcho)")
                existingSummary.editions.add(
                        EditionOfEvent(
                                senderId = event.senderId ?: "",
                                eventId = event.eventId,
                                content = ContentMapper.map(newContent),
                                timestamp = if (isLocalEcho) {
                                    System.currentTimeMillis()
                                } else {
                                    
                                    event.originServerTs ?: System.currentTimeMillis()
                                },
                                isLocalEcho = isLocalEcho
                        )
                )
            }
        }

        if (!isLocalEcho) {
            val replaceEvent = TimelineEventEntity
                    .where(realm, roomId, eventId)
                    .equalTo(TimelineEventEntityFields.OWNED_BY_THREAD_CHUNK, false)
                    .findFirst()
            handleThreadSummaryEdition(editedEvent, replaceEvent, existingSummary?.editions)
        }
    }

    
    private fun handleThreadSummaryEdition(editedEvent: EventEntity?,
                                           replaceEvent: TimelineEventEntity?,
                                           editions: List<EditionOfEvent>?) {
        replaceEvent ?: return
        editedEvent ?: return
        editedEvent.findRootThreadEvent()?.apply {
            val threadSummaryEventId = threadSummaryLatestMessage?.eventId
            if (editedEvent.eventId == threadSummaryEventId || editions?.any { it.eventId == threadSummaryEventId } == true) {
                
                
                threadSummaryLatestMessage = replaceEvent
            }
        }
    }

    private fun handleResponse(realm: Realm,
                               event: Event,
                               content: MessagePollResponseContent,
                               roomId: String,
                               isLocalEcho: Boolean,
                               relatedEventId: String? = null) {
        val eventId = event.eventId ?: return
        val senderId = event.senderId ?: return
        val targetEventId = relatedEventId ?: content.relatesTo?.eventId ?: return
        val eventTimestamp = event.originServerTs ?: return

        val targetPollContent = getPollContent(roomId, targetEventId) ?: return

        
        var existing = EventAnnotationsSummaryEntity.where(realm, roomId, targetEventId).findFirst()
        if (existing == null) {
            Timber.v("## POLL creating new relation summary for $targetEventId")
            existing = EventAnnotationsSummaryEntity.create(realm, roomId, targetEventId)
        }

        
        val existingPollSummary = existing.pollResponseSummary
                ?: realm.createObject(PollResponseAggregatedSummaryEntity::class.java).also {
                    existing.pollResponseSummary = it
                }

        val closedTime = existingPollSummary.closedTime
        if (closedTime != null && eventTimestamp > closedTime) {
            Timber.v("## POLL is closed ignore event poll:$targetEventId, event :${event.eventId}")
            return
        }

        val currentModel = ContentMapper.map(existingPollSummary.aggregatedContent).toModel<PollSummaryContent>()

        if (existingPollSummary.sourceEvents.contains(eventId)) {
            
            Timber.v("## POLL  ignoring event for summary, it's known eventId:$eventId")
            return
        }
        val txId = event.unsignedData?.transactionId
        
        if (!isLocalEcho && existingPollSummary.sourceLocalEchoEvents.contains(txId)) {
            
            Timber.v("## POLL  Receiving remote echo of response eventId:$eventId")
            existingPollSummary.sourceLocalEchoEvents.remove(txId)
            existingPollSummary.sourceEvents.add(event.eventId)
            return
        }

        val option = content.getBestResponse()?.answers?.first() ?: return Unit.also {
            Timber.d("## POLL Ignoring malformed response no option eventId:$eventId content: ${event.content}")
        }

        
        if (!targetPollContent.getBestPollCreationInfo()?.answers?.map { it.id }?.contains(option).orFalse()) {
            Timber.v("## POLL $targetEventId doesn't contain option $option")
            return
        }

        val votes = currentModel?.votes.orEmpty().toMutableList()

        var myVote: String? = null
        val existingVoteIndex = votes.indexOfFirst { it.userId == senderId }
        if (existingVoteIndex != -1) {
            
            val existingVote = votes[existingVoteIndex]
            if (existingVote.voteTimestamp < eventTimestamp) {
                
                votes[existingVoteIndex] = VoteInfo(senderId, option, eventTimestamp)
                if (userId == senderId) {
                    myVote = option
                }
                Timber.v("## POLL adding vote $option for user $senderId in poll :$targetEventId ")
            } else {
                Timber.v("## POLL Ignoring vote (older than known one)  eventId:$eventId ")
            }
        } else {
            votes.add(VoteInfo(senderId, option, eventTimestamp))
            if (userId == senderId) {
                myVote = option
            }
            Timber.v("## POLL adding vote $option for user $senderId in poll :$targetEventId ")
        }

        
        val totalVotes = votes.size
        val newVotesSummary = votes
                .groupBy({ it.option }, { it.userId })
                .mapValues {
                    VoteSummary(
                            total = it.value.size,
                            percentage = if (totalVotes == 0 && it.value.isEmpty()) 0.0 else it.value.size.toDouble() / totalVotes
                    )
                }
        val newWinnerVoteCount = newVotesSummary.maxOf { it.value.total }

        if (isLocalEcho) {
            existingPollSummary.sourceLocalEchoEvents.add(eventId)
        } else {
            existingPollSummary.sourceEvents.add(eventId)
        }

        val newSumModel = PollSummaryContent(
                myVote = myVote,
                votes = votes,
                votesSummary = newVotesSummary,
                totalVotes = totalVotes,
                winnerVoteCount = newWinnerVoteCount
        )

        existingPollSummary.aggregatedContent = ContentMapper.map(newSumModel.toContent())
    }

    private fun handleEndPoll(realm: Realm,
                              event: Event,
                              content: MessageEndPollContent,
                              roomId: String,
                              isLocalEcho: Boolean) {
        val pollEventId = content.relatesTo?.eventId ?: return
        val pollOwnerId = getPollEvent(roomId, pollEventId)?.root?.senderId
        val isPollOwner = pollOwnerId == event.senderId
        val powerLevelsHelper = stateEventDataSource.getStateEvent(roomId, EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.NoCondition)
                ?.content?.toModel<PowerLevelsContent>()
                ?.let { PowerLevelsHelper(it) }

        if (!isPollOwner && !powerLevelsHelper?.isUserAbleToRedact(event.senderId ?: "").orFalse()) {
            Timber.v("## Received poll.end event $pollEventId but user ${event.senderId} doesn't have enough power level in room $roomId")
            return
        }

        var existingPoll = EventAnnotationsSummaryEntity.where(realm, roomId, pollEventId).findFirst()
        if (existingPoll == null) {
            Timber.v("## POLL creating new relation summary for $pollEventId")
            existingPoll = EventAnnotationsSummaryEntity.create(realm, roomId, pollEventId)
        }

        
        val existingPollSummary = existingPoll.pollResponseSummary
                ?: realm.createObject(PollResponseAggregatedSummaryEntity::class.java).also {
                    existingPoll.pollResponseSummary = it
                }

        val txId = event.unsignedData?.transactionId
        existingPollSummary.closedTime = event.originServerTs

        
        if (!isLocalEcho && existingPollSummary.sourceLocalEchoEvents.contains(txId)) {
            
            Timber.v("## POLL  Receiving remote echo of response eventId:$pollEventId")
            existingPollSummary.sourceLocalEchoEvents.remove(txId)
            existingPollSummary.sourceEvents.add(event.eventId)
        }
    }

    private fun getPollEvent(roomId: String, eventId: String): TimelineEvent? {
        val session = sessionManager.getSessionComponent(sessionId)?.session()
        return session?.getRoom(roomId)?.getTimelineEvent(eventId) ?: return null.also {
            Timber.v("## POLL target poll event $eventId not found in room $roomId")
        }
    }

    private fun getPollContent(roomId: String, eventId: String): MessagePollContent? {
        val pollEvent = getPollEvent(roomId, eventId) ?: return null

        return pollEvent.getLastMessageContent() as? MessagePollContent ?: return null.also {
            Timber.v("## POLL target poll event $eventId content is malformed")
        }
    }

    private fun handleInitialAggregatedRelations(realm: Realm,
                                                 event: Event,
                                                 roomId: String,
                                                 aggregation: AggregatedAnnotation) {
        if (SHOULD_HANDLE_SERVER_AGREGGATION) {
            aggregation.chunk?.forEach {
                if (it.type == EventType.REACTION) {
                    val eventId = event.eventId ?: ""
                    val existing = EventAnnotationsSummaryEntity.where(realm, roomId, eventId).findFirst()
                    if (existing == null) {
                        val eventSummary = EventAnnotationsSummaryEntity.create(realm, roomId, eventId)
                        val sum = realm.createObject(ReactionAggregatedSummaryEntity::class.java)
                        sum.key = it.key
                        sum.firstTimestamp = event.originServerTs
                                ?: 0 
                        sum.count = it.count
                        eventSummary.reactionsSummary.add(sum)
                    } else {
                        
                    }
                }
            }
        }
    }

    private fun handleReaction(realm: Realm,
                               event: Event,
                               roomId: String,
                               isLocalEcho: Boolean) {
        val content = event.content.toModel<ReactionContent>()
        if (content == null) {
            Timber.e("Malformed reaction content ${event.content}")
            return
        }
        
        if (RelationType.ANNOTATION == content.relatesTo?.type) {
            val reaction = content.relatesTo.key
            val relatedEventID = content.relatesTo.eventId
            val reactionEventId = event.eventId
            Timber.v("Reaction $reactionEventId relates to $relatedEventID")
            val eventSummary = EventAnnotationsSummaryEntity.getOrCreate(realm, roomId, relatedEventID)

            var sum = eventSummary.reactionsSummary.find { it.key == reaction }
            val txId = event.unsignedData?.transactionId
            if (isLocalEcho && txId.isNullOrBlank()) {
                Timber.w("Received a local echo with no transaction ID")
            }
            if (sum == null) {
                sum = realm.createObject(ReactionAggregatedSummaryEntity::class.java)
                sum.key = reaction
                sum.firstTimestamp = event.originServerTs ?: 0
                if (isLocalEcho) {
                    Timber.v("Adding local echo reaction")
                    sum.sourceLocalEcho.add(txId)
                    sum.count = 1
                } else {
                    Timber.v("Adding synced reaction")
                    sum.count = 1
                    sum.sourceEvents.add(reactionEventId)
                }
                sum.addedByMe = sum.addedByMe || (userId == event.senderId)
                eventSummary.reactionsSummary.add(sum)
            } else {
                
                if (!sum.sourceEvents.contains(reactionEventId)) {
                    
                    if (!isLocalEcho && sum.sourceLocalEcho.contains(txId)) {
                        
                        Timber.v("Ignoring synced of local echo for reaction")
                        sum.sourceLocalEcho.remove(txId)
                        sum.sourceEvents.add(reactionEventId)
                    } else {
                        sum.count += 1
                        if (isLocalEcho) {
                            Timber.v("Adding local echo reaction")
                            sum.sourceLocalEcho.add(txId)
                        } else {
                            Timber.v("Adding synced reaction")
                            sum.sourceEvents.add(reactionEventId)
                        }

                        sum.addedByMe = sum.addedByMe || (userId == event.senderId)
                    }
                }
            }
        } else {
            Timber.e("Unknown relation type ${content.relatesTo?.type} for event ${event.eventId}")
        }
    }

    
    private fun handleRedactionOfReplace(realm: Realm,
                                         redacted: EventEntity,
                                         relatedEventId: String) {
        Timber.d("Handle redaction of m.replace")
        val eventSummary = EventAnnotationsSummaryEntity.where(realm, redacted.roomId, relatedEventId).findFirst()
        if (eventSummary == null) {
            Timber.w("Redaction of a replace targeting an unknown event $relatedEventId")
            return
        }
        val sourceToDiscard = eventSummary.editSummary?.editions?.firstOrNull { it.eventId == redacted.eventId }
        if (sourceToDiscard == null) {
            Timber.w("Redaction of a replace that was not known in aggregation $sourceToDiscard")
            return
        }
        
        sourceToDiscard.deleteFromRealm()
    }

    private fun handleReactionRedact(realm: Realm,
                                     eventToPrune: EventEntity) {
        Timber.v("REDACTION of reaction ${eventToPrune.eventId}")
        
        val reactionContent: ReactionContent = EventMapper.map(eventToPrune).content.toModel() ?: return
        val eventThatWasReacted = reactionContent.relatesTo?.eventId ?: return

        val reactionKey = reactionContent.relatesTo.key
        Timber.v("REMOVE reaction for key $reactionKey")
        val summary = EventAnnotationsSummaryEntity.where(realm, eventToPrune.roomId, eventThatWasReacted).findFirst()
        if (summary != null) {
            summary.reactionsSummary.where()
                    .equalTo(ReactionAggregatedSummaryEntityFields.KEY, reactionKey)
                    .findFirst()?.let { aggregation ->
                        Timber.v("Find summary for key with  ${aggregation.sourceEvents.size} known reactions (count:${aggregation.count})")
                        Timber.v("Known reactions  ${aggregation.sourceEvents.joinToString(",")}")
                        if (aggregation.sourceEvents.contains(eventToPrune.eventId)) {
                            Timber.v("REMOVE reaction for key $reactionKey")
                            aggregation.sourceEvents.remove(eventToPrune.eventId)
                            Timber.v("Known reactions after  ${aggregation.sourceEvents.joinToString(",")}")
                            aggregation.count = aggregation.count - 1
                            if (eventToPrune.sender == userId) {
                                
                                aggregation.addedByMe = false
                            }
                            if (aggregation.count == 0) {
                                
                                aggregation.deleteFromRealm()
                            }
                        } else {
                            Timber.e("## Cannot remove summary from count, corresponding reaction ${eventToPrune.eventId} is not known")
                        }
                    }
        } else {
            Timber.e("## Cannot find summary for key $reactionKey")
        }
    }

    private fun handleVerification(realm: Realm, event: Event, roomId: String, isLocalEcho: Boolean, relatedEventId: String) {
        val eventSummary = EventAnnotationsSummaryEntity.getOrCreate(realm, roomId, relatedEventId)

        val verifSummary = eventSummary.referencesSummaryEntity
                ?: ReferencesAggregatedSummaryEntity.create(realm, relatedEventId).also {
                    eventSummary.referencesSummaryEntity = it
                }

        val txId = event.unsignedData?.transactionId

        if (!isLocalEcho && verifSummary.sourceLocalEcho.contains(txId)) {
            
        } else {
            ContentMapper.map(verifSummary.content)?.toModel<ReferencesAggregatedContent>()
            var data = ContentMapper.map(verifSummary.content)?.toModel<ReferencesAggregatedContent>()
                    ?: ReferencesAggregatedContent(VerificationState.REQUEST)
            
            
            val currentState = data.verificationState
            val newState = when (event.getClearType()) {
                EventType.KEY_VERIFICATION_START,
                EventType.KEY_VERIFICATION_ACCEPT,
                EventType.KEY_VERIFICATION_READY,
                EventType.KEY_VERIFICATION_KEY,
                EventType.KEY_VERIFICATION_MAC    -> currentState.toState(VerificationState.WAITING)
                EventType.KEY_VERIFICATION_CANCEL -> currentState.toState(if (event.senderId == userId) {
                    VerificationState.CANCELED_BY_ME
                } else {
                    VerificationState.CANCELED_BY_OTHER
                })
                EventType.KEY_VERIFICATION_DONE   -> currentState.toState(VerificationState.DONE)
                else                              -> VerificationState.REQUEST
            }

            data = data.copy(verificationState = newState)
            verifSummary.content = ContentMapper.map(data.toContent())
        }

        if (isLocalEcho) {
            verifSummary.sourceLocalEcho.add(event.eventId)
        } else {
            verifSummary.sourceLocalEcho.remove(txId)
            verifSummary.sourceEvents.add(event.eventId)
        }
    }
}
