

package org.matrix.android.sdk.internal.crypto.tasks

import org.matrix.android.sdk.internal.crypto.api.CryptoApi
import org.matrix.android.sdk.internal.crypto.model.rest.KeyChangesResponse
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetKeyChangesTask : Task<GetKeyChangesTask.Params, KeyChangesResponse> {
    data class Params(
            
            val from: String,
            
            val to: String
    )
}

internal class DefaultGetKeyChangesTask @Inject constructor(
        private val cryptoApi: CryptoApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetKeyChangesTask {

    override suspend fun execute(params: GetKeyChangesTask.Params): KeyChangesResponse {
        return executeRequest(globalErrorReceiver) {
            cryptoApi.getKeyChanges(params.from, params.to)
        }
    }
}
