
package org.matrix.android.sdk.internal.session.room.relation

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.zhuinden.monarchy.Monarchy
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.EventAnnotationsSummary
import org.matrix.android.sdk.api.session.room.model.message.PollType
import org.matrix.android.sdk.api.session.room.model.relation.RelationService
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.util.Cancelable
import org.matrix.android.sdk.api.util.NoOpCancellable
import org.matrix.android.sdk.api.util.Optional
import org.matrix.android.sdk.api.util.toOptional
import org.matrix.android.sdk.internal.database.mapper.asDomain
import org.matrix.android.sdk.internal.database.model.EventAnnotationsSummaryEntity
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.session.room.send.LocalEchoEventFactory
import org.matrix.android.sdk.internal.session.room.send.queue.EventSenderProcessor
import org.matrix.android.sdk.internal.session.room.timeline.TimelineEventDataSource
import org.matrix.android.sdk.internal.util.fetchCopyMap
import timber.log.Timber

internal class DefaultRelationService @AssistedInject constructor(
        @Assisted private val roomId: String,
        private val eventEditor: EventEditor,
        private val eventSenderProcessor: EventSenderProcessor,
        private val eventFactory: LocalEchoEventFactory,
        private val findReactionEventForUndoTask: FindReactionEventForUndoTask,
        private val fetchEditHistoryTask: FetchEditHistoryTask,
        private val timelineEventDataSource: TimelineEventDataSource,
        @SessionDatabase private val monarchy: Monarchy
) : RelationService {

    @AssistedFactory
    interface Factory {
        fun create(roomId: String): DefaultRelationService
    }

    override fun sendReaction(targetEventId: String, reaction: String): Cancelable {
        val targetTimelineEvent = timelineEventDataSource.getTimelineEvent(roomId, targetEventId)
        return if (targetTimelineEvent
                        ?.annotations
                        ?.reactionsSummary
                        .orEmpty()
                        .none { it.addedByMe && it.key == reaction }) {
            val event = eventFactory.createReactionEvent(roomId, targetEventId, reaction)
                    .also { saveLocalEcho(it) }
            eventSenderProcessor.postEvent(event, false )
        } else {
            Timber.w("Reaction already added")
            NoOpCancellable
        }
    }

    override suspend fun undoReaction(targetEventId: String, reaction: String): Cancelable {
        val params = FindReactionEventForUndoTask.Params(
                roomId,
                targetEventId,
                reaction
        )

        val data = findReactionEventForUndoTask.executeRetry(params, Int.MAX_VALUE)

        return if (data.redactEventId == null) {
            Timber.w("Cannot find reaction to undo (not yet synced?)")
            
            NoOpCancellable
        } else {
            val redactEvent = eventFactory.createRedactEvent(roomId, data.redactEventId, null)
                    .also { saveLocalEcho(it) }
            eventSenderProcessor.postRedaction(redactEvent, null)
        }
    }

    override fun editPoll(targetEvent: TimelineEvent,
                          pollType: PollType,
                          question: String,
                          options: List<String>): Cancelable {
        return eventEditor.editPoll(targetEvent, pollType, question, options)
    }

    override fun editTextMessage(targetEvent: TimelineEvent,
                                 msgType: String,
                                 newBodyText: CharSequence,
                                 newBodyAutoMarkdown: Boolean,
                                 compatibilityBodyText: String): Cancelable {
        return eventEditor.editTextMessage(targetEvent, msgType, newBodyText, newBodyAutoMarkdown, compatibilityBodyText)
    }

    override fun editCusAwardMessage(targetEvent: TimelineEvent, type: String, status: Int, balance: String?) {
        eventEditor.editCusAwardMessage(targetEvent, type, status,balance)
    }

    override fun editReply(replyToEdit: TimelineEvent,
                           originalTimelineEvent: TimelineEvent,
                           newBodyText: String,
                           compatibilityBodyText: String): Cancelable {
        return eventEditor.editReply(replyToEdit, originalTimelineEvent, newBodyText, compatibilityBodyText)
    }

    override suspend fun fetchEditHistory(eventId: String): List<Event> {
        return fetchEditHistoryTask.execute(FetchEditHistoryTask.Params(roomId, eventId))
    }

    override fun replyToMessage(
            eventReplied: TimelineEvent,
            replyText: CharSequence,
            autoMarkdown: Boolean,
            showInThread: Boolean,
            rootThreadEventId: String?
    ): Cancelable? {
        val event = eventFactory.createReplyTextEvent(
                roomId = roomId,
                eventReplied = eventReplied,
                replyText = replyText,
                autoMarkdown = autoMarkdown,
                rootThreadEventId = rootThreadEventId,
                showInThread = showInThread
        )
                ?.also { saveLocalEcho(it) }
                ?: return null

        return eventSenderProcessor.postEvent(event)
    }

    override fun getEventAnnotationsSummary(eventId: String): EventAnnotationsSummary? {
        return monarchy.fetchCopyMap(
                { EventAnnotationsSummaryEntity.where(it, roomId, eventId).findFirst() },
                { entity, _ ->
                    entity.asDomain()
                }
        )
    }

    override fun getEventAnnotationsSummaryLive(eventId: String): LiveData<Optional<EventAnnotationsSummary>> {
        val liveData = monarchy.findAllMappedWithChanges(
                { EventAnnotationsSummaryEntity.where(it, roomId, eventId) },
                { it.asDomain() }
        )
        return Transformations.map(liveData) { results ->
            results.firstOrNull().toOptional()
        }
    }

    override fun replyInThread(
            rootThreadEventId: String,
            replyInThreadText: CharSequence,
            msgType: String,
            autoMarkdown: Boolean,
            formattedText: String?,
            eventReplied: TimelineEvent?): Cancelable? {
        val event = if (eventReplied != null) {
            
            eventFactory.createReplyTextEvent(
                    roomId = roomId,
                    eventReplied = eventReplied,
                    replyText = replyInThreadText,
                    autoMarkdown = autoMarkdown,
                    rootThreadEventId = rootThreadEventId,
                    showInThread = false
            )
                    ?.also {
                        saveLocalEcho(it)
                    }
                    ?: return null
        } else {
            
            eventFactory.createThreadTextEvent(
                    rootThreadEventId = rootThreadEventId,
                    roomId = roomId,
                    text = replyInThreadText,
                    msgType = msgType,
                    autoMarkdown = autoMarkdown,
                    formattedText = formattedText
            )
                    .also {
                        saveLocalEcho(it)
                    }
        }
        return eventSenderProcessor.postEvent(event)
    }

    
    private fun saveLocalEcho(event: Event) {
        eventFactory.createLocalEcho(event)
    }
}
