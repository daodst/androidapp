

package org.matrix.android.sdk.internal.session.room.send.queue

import org.matrix.android.sdk.api.util.Cancelable
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger


internal abstract class QueuedTask(
        val queueIdentifier: String,
        val taskIdentifier: String
) : Cancelable {

    override fun toString() = "${javaClass.simpleName} queueIdentifier: $queueIdentifier, taskIdentifier:  $taskIdentifier)"

    var retryCount = AtomicInteger(0)

    private var hasBeenCancelled: Boolean = false

    suspend fun execute() {
        if (!isCancelled()) {
            Timber.v("Execute: $this start")
            doExecute()
            Timber.v("Execute: $this finish")
        }
    }

    abstract suspend fun doExecute()

    abstract fun onTaskFailed()

    open fun isCancelled() = hasBeenCancelled

    final override fun cancel() {
        hasBeenCancelled = true
    }
}
