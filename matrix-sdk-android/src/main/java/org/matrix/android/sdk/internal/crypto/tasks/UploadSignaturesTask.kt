
package org.matrix.android.sdk.internal.crypto.tasks

import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.internal.crypto.api.CryptoApi
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface UploadSignaturesTask : Task<UploadSignaturesTask.Params, Unit> {
    data class Params(
            val signatures: Map<String, Map<String, Any>>
    )
}

internal class DefaultUploadSignaturesTask @Inject constructor(
        private val cryptoApi: CryptoApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : UploadSignaturesTask {

    override suspend fun execute(params: UploadSignaturesTask.Params) {
        try {
            val response = executeRequest(
                    globalErrorReceiver,
                    canRetry = true,
                    maxRetriesCount = 10
            ) {
                cryptoApi.uploadSignatures(params.signatures)
            }
            if (response.failures?.isNotEmpty() == true) {
                throw Throwable(response.failures.toString())
            }
            return
        } catch (f: Failure) {
            throw f
        }
    }
}
