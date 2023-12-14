

package org.matrix.android.sdk.internal.session.call

import io.realm.Realm
import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.internal.database.model.EventInsertType
import org.matrix.android.sdk.internal.session.EventInsertLiveProcessor
import org.matrix.android.sdk.internal.session.SessionScope
import timber.log.Timber
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

private val loggerTag = LoggerTag("CallEventProcessor", LoggerTag.VOIP)

@SessionScope
internal class CallEventProcessor @Inject constructor(private val callSignalingHandler: CallSignalingHandler) :
        EventInsertLiveProcessor {

    private val allowedTypes = listOf(
            EventType.CALL_ANSWER,
            EventType.CALL_SELECT_ANSWER,
            EventType.CALL_REJECT,
            EventType.CALL_NEGOTIATE,
            EventType.CALL_CANDIDATES,
            EventType.CALL_INVITE,
            EventType.CALL_HANGUP,
            EventType.ENCRYPTED,
            EventType.CALL_ASSERTED_IDENTITY,
            EventType.CALL_ASSERTED_IDENTITY_PREFIX
    )


    private val eventsToPostProcess = CopyOnWriteArrayList<Event>()

    override fun shouldProcess(eventId: String, eventType: String, insertType: EventInsertType): Boolean {
        if (insertType != EventInsertType.INCREMENTAL_SYNC) {
            return false
        }
        return allowedTypes.contains(eventType)
    }

    override suspend fun process(realm: Realm, event: Event) {
        eventsToPostProcess.add(event)
    }

    fun shouldProcessFastLane(eventType: String): Boolean {
        return eventType == EventType.CALL_INVITE
    }

    fun processFastLane(event: Event) {
        dispatchToCallSignalingHandlerIfNeeded(event)
    }

    override suspend fun onPostProcess() {
        eventsToPostProcess.forEach {
            dispatchToCallSignalingHandlerIfNeeded(it)
        }
        eventsToPostProcess.clear()
    }

    private fun dispatchToCallSignalingHandlerIfNeeded(event: Event) {
        event.roomId ?: return Unit.also {
            Timber.tag(loggerTag.value).w("Event with no room id ${event.eventId}")
        }
        callSignalingHandler.onCallEvent(event)
    }
}
