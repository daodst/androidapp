

package org.matrix.android.sdk.internal.network

import org.matrix.android.sdk.internal.network.executeRequest as internalExecuteRequest

internal interface RequestExecutor {
    suspend fun <DATA> executeRequest(globalErrorReceiver: GlobalErrorReceiver?,
                                      canRetry: Boolean = false,
                                      maxDelayBeforeRetry: Long = 32_000L,
                                      maxRetriesCount: Int = 4,
                                      requestBlock: suspend () -> DATA): DATA
}

internal object DefaultRequestExecutor : RequestExecutor {
    override suspend fun <DATA> executeRequest(globalErrorReceiver: GlobalErrorReceiver?,
                                               canRetry: Boolean,
                                               maxDelayBeforeRetry: Long,
                                               maxRetriesCount: Int,
                                               requestBlock: suspend () -> DATA): DATA {
        return internalExecuteRequest(globalErrorReceiver, canRetry, maxDelayBeforeRetry, maxRetriesCount, requestBlock)
    }
}
