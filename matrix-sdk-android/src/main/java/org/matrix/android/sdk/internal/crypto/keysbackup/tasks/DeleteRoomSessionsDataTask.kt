

package org.matrix.android.sdk.internal.crypto.keysbackup.tasks

import org.matrix.android.sdk.internal.crypto.keysbackup.api.RoomKeysApi
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface DeleteRoomSessionsDataTask : Task<DeleteRoomSessionsDataTask.Params, Unit> {
    data class Params(
            val roomId: String,
            val version: String
    )
}

internal class DefaultDeleteRoomSessionsDataTask @Inject constructor(
        private val roomKeysApi: RoomKeysApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : DeleteRoomSessionsDataTask {

    override suspend fun execute(params: DeleteRoomSessionsDataTask.Params) {
        return executeRequest(globalErrorReceiver) {
            roomKeysApi.deleteRoomSessionsData(
                    params.roomId,
                    params.version)
        }
    }
}
