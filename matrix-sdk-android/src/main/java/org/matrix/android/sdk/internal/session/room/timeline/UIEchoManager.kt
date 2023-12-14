

package org.matrix.android.sdk.internal.session.room.timeline

import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.EventAnnotationsSummary
import org.matrix.android.sdk.api.session.room.model.ReactionAggregatedSummary
import org.matrix.android.sdk.api.session.room.model.relation.ReactionContent
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import timber.log.Timber
import java.util.Collections

internal class UIEchoManager(private val listener: Listener) {

    interface Listener {
        fun rebuildEvent(eventId: String, builder: (TimelineEvent) -> TimelineEvent?): Boolean
    }

    private val inMemorySendingEvents = Collections.synchronizedList<TimelineEvent>(ArrayList())

    fun getInMemorySendingEvents(): List<TimelineEvent> {
        return inMemorySendingEvents.toList()
    }

    
    private val inMemorySendingStates = Collections.synchronizedMap<String, SendState>(HashMap())

    private val inMemoryReactions = Collections.synchronizedMap<String, MutableList<ReactionUiEchoData>>(HashMap())

    fun onSentEventsInDatabase(eventIds: List<String>) {
        
        eventIds.forEach { eventId ->
            inMemorySendingEvents.removeAll { eventId == it.eventId }
        }
        inMemoryReactions.forEach { (_, uiEchoData) ->
            uiEchoData.removeAll { data ->
                
                
                eventIds.find { it == data.localEchoId } == null
            }
        }
    }

    fun onSendStateUpdated(eventId: String, sendState: SendState): Boolean {
        val existingState = inMemorySendingStates[eventId]
        inMemorySendingStates[eventId] = sendState
        return existingState != sendState
    }

    fun onLocalEchoCreated(timelineEvent: TimelineEvent): Boolean {
        when (timelineEvent.root.getClearType()) {
            EventType.REDACTION -> {
            }
            EventType.REACTION  -> {
                val content: ReactionContent? = timelineEvent.root.content?.toModel<ReactionContent>()
                if (RelationType.ANNOTATION == content?.relatesTo?.type) {
                    val reaction = content.relatesTo.key
                    val relatedEventID = content.relatesTo.eventId
                    inMemoryReactions.getOrPut(relatedEventID) { mutableListOf() }
                            .add(
                                    ReactionUiEchoData(
                                            localEchoId = timelineEvent.eventId,
                                            reactedOnEventId = relatedEventID,
                                            reaction = reaction
                                    )
                            )
                    listener.rebuildEvent(relatedEventID) {
                        decorateEventWithReactionUiEcho(it)
                    }
                }
            }
        }
        Timber.v("On local echo created: ${timelineEvent.eventId}")
        inMemorySendingEvents.add(0, timelineEvent)
        return true
    }

    fun decorateEventWithReactionUiEcho(timelineEvent: TimelineEvent): TimelineEvent {
        val relatedEventID = timelineEvent.eventId
        val contents = inMemoryReactions[relatedEventID] ?: return timelineEvent

        var existingAnnotationSummary = timelineEvent.annotations ?: EventAnnotationsSummary(
                relatedEventID
        )
        val updateReactions = existingAnnotationSummary.reactionsSummary.toMutableList()

        contents.forEach { uiEchoReaction ->
            val indexOfExistingReaction = updateReactions.indexOfFirst { it.key == uiEchoReaction.reaction }
            if (indexOfExistingReaction == -1) {
                
                ReactionAggregatedSummary(
                        key = uiEchoReaction.reaction,
                        count = 1,
                        addedByMe = true,
                        firstTimestamp = System.currentTimeMillis(),
                        sourceEvents = emptyList(),
                        localEchoEvents = listOf(uiEchoReaction.localEchoId)
                ).let { updateReactions.add(it) }
            } else {
                
                val existing = updateReactions[indexOfExistingReaction]
                if (!existing.localEchoEvents.contains(uiEchoReaction.localEchoId)) {
                    updateReactions.remove(existing)
                    
                    ReactionAggregatedSummary(
                            key = existing.key,
                            count = existing.count + 1,
                            addedByMe = true,
                            firstTimestamp = existing.firstTimestamp,
                            sourceEvents = existing.sourceEvents,
                            localEchoEvents = existing.localEchoEvents + uiEchoReaction.localEchoId

                    ).let { updateReactions.add(indexOfExistingReaction, it) }
                }
            }
        }

        existingAnnotationSummary = existingAnnotationSummary.copy(
                reactionsSummary = updateReactions
        )
        return timelineEvent.copy(
                annotations = existingAnnotationSummary
        )
    }

    fun updateSentStateWithUiEcho(timelineEvent: TimelineEvent): TimelineEvent {
        if (timelineEvent.root.sendState.isSent()) return timelineEvent
        val inMemoryState = inMemorySendingStates[timelineEvent.eventId] ?: return timelineEvent
        
        return timelineEvent.copy(
                root = timelineEvent.root.copyAll()
                        .also { it.sendState = inMemoryState }
        )
    }

    fun onSyncedEvent(transactionId: String?) {
        val sendingEvent = inMemorySendingEvents.find {
            it.eventId == transactionId
        }
        inMemorySendingEvents.remove(sendingEvent)
        
        inMemoryReactions.forEach { (_, u) ->
            u.filterNot { it.localEchoId == transactionId }
        }
        inMemorySendingStates.remove(transactionId)
    }
}
