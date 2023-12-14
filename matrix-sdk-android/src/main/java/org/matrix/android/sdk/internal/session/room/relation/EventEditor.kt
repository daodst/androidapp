

package org.matrix.android.sdk.internal.session.room.relation

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.message.MessageCusAwardContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusPledgeAwardContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.message.PollType
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.util.Cancelable
import org.matrix.android.sdk.api.util.NoOpCancellable
import org.matrix.android.sdk.internal.database.mapper.toEntity
import org.matrix.android.sdk.internal.session.room.send.LocalEchoEventFactory
import org.matrix.android.sdk.internal.session.room.send.LocalEchoRepository
import org.matrix.android.sdk.internal.session.room.send.queue.EventSenderProcessor
import timber.log.Timber
import javax.inject.Inject

internal class EventEditor @Inject constructor(private val eventSenderProcessor: EventSenderProcessor, private val eventFactory: LocalEchoEventFactory, private val localEchoRepository: LocalEchoRepository) {

    fun editCusAwardMessage(targetEvent: TimelineEvent, msgType: String, status: Int, balance: String?) {
        val roomId = targetEvent.roomId
        val messageContent = targetEvent.getLastMessageContent()

        val editedEvent = if (MessageType.MSGTYPE_AWARD_TEXT == msgType) {
            val content = messageContent as MessageCusAwardContent
            eventFactory.createAwardEvent(roomId, msgType, content.type, status, balance).copy(
                    eventId = targetEvent.eventId, originServerTs = targetEvent.root.originServerTs
            )
        } else {
            val content = messageContent as MessageCusPledgeAwardContent
            eventFactory.createPledgeAwardEvent(
                    roomId, msgType, true
            ).copy(
                    eventId = targetEvent.eventId, originServerTs = targetEvent.root.originServerTs
            )
        }
        updateCusEchoWithEvent(roomId, targetEvent.eventId, editedEvent)
    }

    private fun updateCusEchoWithEvent(roomId: String, echoEventId: String, editedEvent: Event) {
        val editedEventEntity = editedEvent.toEntity(roomId, SendState.SYNCED, System.currentTimeMillis())
        localEchoRepository.updateEchoAsync(echoEventId) { _, entity ->
            entity.content = editedEventEntity.content
            entity.ageLocalTs = editedEventEntity.ageLocalTs
            entity.age = editedEventEntity.age
            entity.originServerTs = editedEventEntity.originServerTs
            entity.sendState = editedEventEntity.sendState
            entity.sendStateDetails = editedEventEntity.sendStateDetails
        }
    }

    suspend fun updateEchoWithEvent(roomId: String, echoEventId: String, editedEvent: Event) {
        val editedEventEntity = editedEvent.toEntity(roomId, SendState.UNSENT, System.currentTimeMillis())
        localEchoRepository.updateEcho(echoEventId) { _, entity ->
            entity.content = editedEventEntity.content
            entity.ageLocalTs = editedEventEntity.ageLocalTs
            entity.age = editedEventEntity.age
            entity.originServerTs = editedEventEntity.originServerTs
            entity.sendState = editedEventEntity.sendState
            entity.sendStateDetails = editedEventEntity.sendStateDetails
        }
    }

    fun editTextMessage(targetEvent: TimelineEvent, msgType: String, newBodyText: CharSequence, newBodyAutoMarkdown: Boolean, compatibilityBodyText: String): Cancelable {
        val roomId = targetEvent.roomId
        if (targetEvent.root.sendState.hasFailed()) {
            
            val editedEvent = eventFactory.createTextEvent(roomId, msgType, newBodyText, newBodyAutoMarkdown).copy(
                    eventId = targetEvent.eventId
            )
            return sendFailedEvent(targetEvent, editedEvent)
        } else if (targetEvent.root.sendState.isSent()) {
            val event = eventFactory.createReplaceTextEvent(roomId, targetEvent.eventId, newBodyText, newBodyAutoMarkdown, msgType, compatibilityBodyText)
            return sendReplaceEvent(event)
        } else {
            
            Timber.w("Can't edit a sending event")
            return NoOpCancellable
        }
    }

    fun editPoll(targetEvent: TimelineEvent, pollType: PollType, question: String, options: List<String>): Cancelable {
        val roomId = targetEvent.roomId
        if (targetEvent.root.sendState.hasFailed()) {
            val editedEvent = eventFactory.createPollEvent(roomId, pollType, question, options).copy(
                    eventId = targetEvent.eventId
            )
            return sendFailedEvent(targetEvent, editedEvent)
        } else if (targetEvent.root.sendState.isSent()) {
            val event = eventFactory.createPollReplaceEvent(roomId, pollType, targetEvent.eventId, question, options)
            return sendReplaceEvent(event)
        } else {
            Timber.w("Can't edit a sending event")
            return NoOpCancellable
        }
    }

    private fun sendFailedEvent(targetEvent: TimelineEvent, editedEvent: Event): Cancelable {
        val roomId = targetEvent.roomId
        updateFailedEchoWithEvent(roomId, targetEvent.eventId, editedEvent)
        return eventSenderProcessor.postEvent(editedEvent)
    }

    private fun sendReplaceEvent(editedEvent: Event): Cancelable {
        localEchoRepository.createLocalEcho(editedEvent)
        return eventSenderProcessor.postEvent(editedEvent)
    }

    fun editReply(replyToEdit: TimelineEvent, originalTimelineEvent: TimelineEvent, newBodyText: String, compatibilityBodyText: String): Cancelable {
        val roomId = replyToEdit.roomId
        if (replyToEdit.root.sendState.hasFailed()) {
            
            val editedEvent = eventFactory.createReplyTextEvent(
                    roomId = roomId, eventReplied = originalTimelineEvent, replyText = newBodyText, autoMarkdown = false, showInThread = false
            )?.copy(
                    eventId = replyToEdit.eventId
            ) ?: return NoOpCancellable
            updateFailedEchoWithEvent(roomId, replyToEdit.eventId, editedEvent)
            return eventSenderProcessor.postEvent(editedEvent)
        } else if (replyToEdit.root.sendState.isSent()) {
            val event = eventFactory.createReplaceTextOfReply(
                    roomId, replyToEdit, originalTimelineEvent, newBodyText, true, MessageType.MSGTYPE_TEXT, compatibilityBodyText
            ).also { localEchoRepository.createLocalEcho(it) }
            return eventSenderProcessor.postEvent(event)
        } else {
            
            Timber.w("Can't edit a sending event")
            return NoOpCancellable
        }
    }

    private fun updateFailedEchoWithEvent(roomId: String, failedEchoEventId: String, editedEvent: Event) {
        val editedEventEntity = editedEvent.toEntity(roomId, SendState.UNSENT, System.currentTimeMillis())
        localEchoRepository.updateEchoAsync(failedEchoEventId) { _, entity ->
            entity.content = editedEventEntity.content
            entity.ageLocalTs = editedEventEntity.ageLocalTs
            entity.age = editedEventEntity.age
            entity.originServerTs = editedEventEntity.originServerTs
            entity.sendState = editedEventEntity.sendState
            entity.sendStateDetails = editedEventEntity.sendStateDetails
        }
    }
}
