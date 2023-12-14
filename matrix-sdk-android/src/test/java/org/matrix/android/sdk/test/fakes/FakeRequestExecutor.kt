

package org.matrix.android.sdk.test.fakes

import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.RequestExecutor

internal class FakeRequestExecutor : RequestExecutor {

    override suspend fun <DATA> executeRequest(globalErrorReceiver: GlobalErrorReceiver?,
                                               canRetry: Boolean,
                                               maxDelayBeforeRetry: Long,
                                               maxRetriesCount: Int,
                                               requestBlock: suspend () -> DATA): DATA {
        return requestBlock()
    }
}
