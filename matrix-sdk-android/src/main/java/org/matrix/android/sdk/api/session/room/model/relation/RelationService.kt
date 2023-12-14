
package org.matrix.android.sdk.api.session.room.model.relation

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.EventAnnotationsSummary
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.message.PollType
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.util.Cancelable
import org.matrix.android.sdk.api.util.Optional


interface RelationService {

    
    fun sendReaction(targetEventId: String,
                     reaction: String): Cancelable

    
    suspend fun undoReaction(targetEventId: String,
                             reaction: String): Cancelable

    
    fun editPoll(targetEvent: TimelineEvent,
                 pollType: PollType,
                 question: String,
                 options: List<String>): Cancelable

    
    fun editTextMessage(targetEvent: TimelineEvent,
                        msgType: String,
                        newBodyText: CharSequence,
                        newBodyAutoMarkdown: Boolean,
                        compatibilityBodyText: String = "* $newBodyText"): Cancelable

    
    fun editCusAwardMessage(
            targetEvent: TimelineEvent,
            type: String,
            status: Int,
            balance: String?,
    )

    
    fun editReply(replyToEdit: TimelineEvent,
                  originalTimelineEvent: TimelineEvent,
                  newBodyText: String,
                  compatibilityBodyText: String = "* $newBodyText"): Cancelable

    
    suspend fun fetchEditHistory(eventId: String): List<Event>

    
    fun replyToMessage(eventReplied: TimelineEvent,
                       replyText: CharSequence,
                       autoMarkdown: Boolean = false,
                       showInThread: Boolean = false,
                       rootThreadEventId: String? = null
    ): Cancelable?

    
    fun getEventAnnotationsSummary(eventId: String): EventAnnotationsSummary?

    
    fun getEventAnnotationsSummaryLive(eventId: String): LiveData<Optional<EventAnnotationsSummary>>

    
    fun replyInThread(rootThreadEventId: String,
                      replyInThreadText: CharSequence,
                      msgType: String = MessageType.MSGTYPE_TEXT,
                      autoMarkdown: Boolean = false,
                      formattedText: String? = null,
                      eventReplied: TimelineEvent? = null): Cancelable?
}
