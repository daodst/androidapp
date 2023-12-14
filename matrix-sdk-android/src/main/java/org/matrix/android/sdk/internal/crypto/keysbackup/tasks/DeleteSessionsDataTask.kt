

package org.matrix.android.sdk.internal.crypto.keysbackup.tasks

import org.matrix.android.sdk.internal.crypto.keysbackup.api.RoomKeysApi
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface DeleteSessionsDataTask : Task<DeleteSessionsDataTask.Params, Unit> {
    data class Params(
            val version: String
    )
}

internal class DefaultDeleteSessionsDataTask @Inject constructor(
        private val roomKeysApi: RoomKeysApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : DeleteSessionsDataTask {

    override suspend fun execute(params: DeleteSessionsDataTask.Params) {
        return executeRequest(globalErrorReceiver) {
            roomKeysApi.deleteSessionsData(params.version)
        }
    }
}
