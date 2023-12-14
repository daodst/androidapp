

package org.matrix.android.sdk.internal.session.room.send.queue

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.matrix.android.sdk.api.auth.data.SessionParams
import org.matrix.android.sdk.api.auth.data.sessionId
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.isLimitExceededError
import org.matrix.android.sdk.api.failure.isTokenError
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.CryptoService
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.sync.SyncState
import org.matrix.android.sdk.api.util.Cancelable
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.task.TaskExecutor
import timber.log.Timber
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject
import kotlin.concurrent.schedule


@Deprecated("You should know use EventSenderProcessorCoroutine instead")
@SessionScope
internal class EventSenderProcessorThread @Inject constructor(
        private val cryptoService: CryptoService,
        private val sessionParams: SessionParams,
        private val queuedTaskFactory: QueuedTaskFactory,
        private val taskExecutor: TaskExecutor,
        private val memento: QueueMemento
) : Thread("SENDER_THREAD_SID_${sessionParams.credentials.sessionId()}"), EventSenderProcessor {

    private fun markAsManaged(task: QueuedTask) {
        memento.track(task)
    }

    private fun markAsFinished(task: QueuedTask) {
        memento.unTrack(task)
    }

    override fun onSessionStarted(session: Session) {
        start()
    }

    override fun onSessionStopped(session: Session) {
        interrupt()
    }

    override fun start() {
        super.start()
        
        
        
        
        
        
        

        tryOrNull {
            taskExecutor.executorScope.launch {
                Timber.d("## Send relaunched pending events on restart")
                memento.restoreTasks(this@EventSenderProcessorThread)
            }
        }
    }

    
    override fun postEvent(event: Event): Cancelable {
        return postEvent(event, event.roomId?.let { cryptoService.isRoomEncrypted(it) } ?: false)
    }

    override fun postEvent(event: Event, encrypt: Boolean): Cancelable {
        val task = queuedTaskFactory.createSendTask(event, encrypt)
        return postTask(task)
    }

    override fun postRedaction(redactionLocalEcho: Event, reason: String?): Cancelable {
        return postRedaction(redactionLocalEcho.eventId!!, redactionLocalEcho.redacts!!, redactionLocalEcho.roomId!!, reason)
    }

    override fun postRedaction(redactionLocalEchoId: String, eventToRedactId: String, roomId: String, reason: String?): Cancelable {
        val task = queuedTaskFactory.createRedactTask(redactionLocalEchoId, eventToRedactId, roomId, reason)
        return postTask(task)
    }

    override fun postTask(task: QueuedTask): Cancelable {
        
        sendingQueue.add(task)
        markAsManaged(task)
        return task
    }

    override fun cancel(eventId: String, roomId: String) {
        (currentTask as? SendEventQueuedTask)
                ?.takeIf { it -> it.event.eventId == eventId && it.event.roomId == roomId }
                ?.cancel()
    }

    companion object {
        private const val RETRY_WAIT_TIME_MS = 10_000L
    }

    private var currentTask: QueuedTask? = null

    private var sendingQueue = LinkedBlockingQueue<QueuedTask>()

    private var networkAvailableLock = Object()
    private var canReachServer = true
    private var retryNoNetworkTask: TimerTask? = null

    override fun run() {
        Timber.v("## SendThread started ts:${System.currentTimeMillis()}")
        try {
            while (!isInterrupted) {
                Timber.v("## SendThread wait for task to process")
                val task = sendingQueue.take()
                        .also { currentTask = it }
                Timber.v("## SendThread Found task to process $task")

                if (task.isCancelled()) {
                    Timber.v("## SendThread send cancelled for $task")
                    
                    continue
                }
                
                while (!canReachServer) {
                    Timber.v("## SendThread cannot reach server, wait ts:${System.currentTimeMillis()}")
                    
                    waitForNetwork()
                    
                }
                Timber.v("## Server is Reachable")
                

                runBlocking {
                    retryLoop@ while (task.retryCount.get() < 3) {
                        try {
                            
                            Timber.v("## SendThread retryLoop for $task retryCount ${task.retryCount}")
                            task.execute()
                            
                            
                            break@retryLoop
                        } catch (exception: Throwable) {
                            when {
                                exception is IOException || exception is Failure.NetworkConnection -> {
                                    canReachServer = false
                                    if (task.retryCount.getAndIncrement() >= 3) task.onTaskFailed()
                                    while (!canReachServer) {
                                        Timber.v("## SendThread retryLoop cannot reach server, wait ts:${System.currentTimeMillis()}")
                                        
                                        waitForNetwork()
                                    }
                                }
                                (exception.isLimitExceededError())                                 -> {
                                    if (task.retryCount.getAndIncrement() >= 3) task.onTaskFailed()
                                    Timber.v("## SendThread retryLoop retryable error for $task reason: ${exception.localizedMessage}")
                                    
                                    
                                    sleep(3_000)
                                    continue@retryLoop
                                }
                                exception.isTokenError()                                           -> {
                                    Timber.v("## SendThread retryLoop retryable TOKEN error, interrupt")
                                    
                                    task.onTaskFailed()
                                    throw InterruptedException()
                                }
                                exception is CancellationException                                 -> {
                                    Timber.v("## SendThread task has been cancelled")
                                    break@retryLoop
                                }
                                else                                                               -> {
                                    Timber.v("## SendThread retryLoop Un-Retryable error, try next task")
                                    
                                    task.onTaskFailed()
                                    break@retryLoop
                                }
                            }
                        }
                    }
                }
                markAsFinished(task)
            }
        } catch (interruptionException: InterruptedException) {
            
            interrupt()
            Timber.v("## InterruptedException!! ${interruptionException.localizedMessage}")
        }
        
        retryNoNetworkTask?.cancel()
        Timber.w("## SendThread finished ${System.currentTimeMillis()}")
    }

    private fun waitForNetwork() {
        retryNoNetworkTask = Timer(SyncState.NoNetwork.toString(), false).schedule(RETRY_WAIT_TIME_MS) {
            synchronized(networkAvailableLock) {
                canReachServer = HomeServerAvailabilityChecker(sessionParams).check().also {
                    Timber.v("## SendThread checkHostAvailable $it")
                }
                networkAvailableLock.notify()
            }
        }
        synchronized(networkAvailableLock) { networkAvailableLock.wait() }
    }
}
