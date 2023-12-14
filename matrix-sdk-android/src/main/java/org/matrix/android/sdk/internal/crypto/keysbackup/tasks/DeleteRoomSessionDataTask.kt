

package org.matrix.android.sdk.internal.crypto.keysbackup.tasks

import org.matrix.android.sdk.internal.crypto.keysbackup.api.RoomKeysApi
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface DeleteRoomSessionDataTask : Task<DeleteRoomSessionDataTask.Params, Unit> {
    data class Params(
            val roomId: String,
            val sessionId: String,
            val version: String
    )
}

internal class DefaultDeleteRoomSessionDataTask @Inject constructor(
        private val roomKeysApi: RoomKeysApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : DeleteRoomSessionDataTask {

    override suspend fun execute(params: DeleteRoomSessionDataTask.Params) {
        return executeRequest(globalErrorReceiver) {
            roomKeysApi.deleteRoomSessionData(
                    params.roomId,
                    params.sessionId,
                    params.version)
        }
    }
}
