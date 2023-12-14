

package org.matrix.android.sdk.internal.crypto.keysbackup.tasks

import org.matrix.android.sdk.internal.crypto.keysbackup.api.RoomKeysApi
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.KeyBackupData
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetRoomSessionDataTask : Task<GetRoomSessionDataTask.Params, KeyBackupData> {
    data class Params(
            val roomId: String,
            val sessionId: String,
            val version: String
    )
}

internal class DefaultGetRoomSessionDataTask @Inject constructor(
        private val roomKeysApi: RoomKeysApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetRoomSessionDataTask {

    override suspend fun execute(params: GetRoomSessionDataTask.Params): KeyBackupData {
        return executeRequest(globalErrorReceiver) {
            roomKeysApi.getRoomSessionData(
                    params.roomId,
                    params.sessionId,
                    params.version)
        }
    }
}
