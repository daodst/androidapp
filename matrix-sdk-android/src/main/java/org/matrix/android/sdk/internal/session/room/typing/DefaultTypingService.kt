

package org.matrix.android.sdk.internal.session.room.typing

import android.os.SystemClock
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.room.typing.TypingService
import timber.log.Timber


internal class DefaultTypingService @AssistedInject constructor(
        @Assisted private val roomId: String,
        private val sendTypingTask: SendTypingTask
) : TypingService {

    @AssistedFactory
    interface Factory {
        fun create(roomId: String): DefaultTypingService
    }

    private val coroutineScope = CoroutineScope(Job())
    private var currentTask: Job? = null

    
    private var userIsTyping = false

    
    private var lastRequestTimestamp: Long = 0

    
    override fun userIsTyping() {
        val now = SystemClock.elapsedRealtime()
        currentTask?.cancel()
        currentTask = coroutineScope.launch {
            if (userIsTyping && now < lastRequestTimestamp + MIN_DELAY_BETWEEN_TWO_USER_IS_TYPING_REQUESTS_MILLIS) {
                Timber.d("Typing: Skip start request")
            } else {
                Timber.d("Typing: Send start request")
                lastRequestTimestamp = now
                sendRequest(true)
            }
            delay(MIN_DELAY_TO_SEND_STOP_TYPING_REQUEST_WHEN_NO_USER_ACTIVITY_MILLIS)
            Timber.d("Typing: auto stop")
            sendRequest(false)
        }
    }

    override fun userStopsTyping() {
        if (!userIsTyping) {
            Timber.d("Typing: Skip stop request")
            return
        }

        Timber.d("Typing: Send stop request")
        lastRequestTimestamp = 0

        currentTask?.cancel()
        currentTask = coroutineScope.launch {
            sendRequest(false)
        }
    }

    private suspend fun sendRequest(isTyping: Boolean) {
        try {
            sendTypingTask.execute(SendTypingTask.Params(roomId, isTyping))
            userIsTyping = isTyping
        } catch (failure: Throwable) {
            
            Timber.w(failure, "Unable to send typing request")
        }
    }

    companion object {
        private const val MIN_DELAY_BETWEEN_TWO_USER_IS_TYPING_REQUESTS_MILLIS = 10_000L
        private const val MIN_DELAY_TO_SEND_STOP_TYPING_REQUEST_WHEN_NO_USER_ACTIVITY_MILLIS = 10_000L
    }
}
