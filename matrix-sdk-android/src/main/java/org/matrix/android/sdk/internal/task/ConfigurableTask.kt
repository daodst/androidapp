

package org.matrix.android.sdk.internal.task

import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.NoOpMatrixCallback
import org.matrix.android.sdk.api.util.Cancelable
import java.util.UUID

internal fun <PARAMS, RESULT> Task<PARAMS, RESULT>.configureWith(params: PARAMS,
                                                                 init: (ConfigurableTask.Builder<PARAMS, RESULT>.() -> Unit) = {}
): ConfigurableTask<PARAMS, RESULT> {
    return ConfigurableTask.Builder(this, params).apply(init).build()
}

internal fun <RESULT> Task<Unit, RESULT>.configureWith(init: (ConfigurableTask.Builder<Unit, RESULT>.() -> Unit) = {}): ConfigurableTask<Unit, RESULT> {
    return configureWith(Unit, init)
}

internal data class ConfigurableTask<PARAMS, RESULT>(
        val task: Task<PARAMS, RESULT>,
        val params: PARAMS,
        val id: UUID,
        val callbackThread: TaskThread,
        val executionThread: TaskThread,
        val callback: MatrixCallback<RESULT>,
        val maxRetryCount: Int = 0

) : Task<PARAMS, RESULT> by task {

    class Builder<PARAMS, RESULT>(
            private val task: Task<PARAMS, RESULT>,
            private val params: PARAMS,
            var id: UUID = UUID.randomUUID(),
            var callbackThread: TaskThread = TaskThread.MAIN,
            var executionThread: TaskThread = TaskThread.IO,
            var retryCount: Int = 0,
            var callback: MatrixCallback<RESULT> = NoOpMatrixCallback()
    ) {

        fun build() = ConfigurableTask(
                task = task,
                params = params,
                id = id,
                callbackThread = callbackThread,
                executionThread = executionThread,
                callback = callback,
                maxRetryCount = retryCount
        )
    }

    fun executeBy(taskExecutor: TaskExecutor): Cancelable {
        return taskExecutor.execute(this)
    }

    override fun toString(): String {
        return "${task.javaClass.name} with ID: $id"
    }
}
