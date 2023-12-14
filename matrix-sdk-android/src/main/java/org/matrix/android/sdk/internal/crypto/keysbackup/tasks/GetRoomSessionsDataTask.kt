

package org.matrix.android.sdk.internal.crypto.keysbackup.tasks

import org.matrix.android.sdk.internal.crypto.keysbackup.api.RoomKeysApi
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.RoomKeysBackupData
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetRoomSessionsDataTask : Task<GetRoomSessionsDataTask.Params, RoomKeysBackupData> {
    data class Params(
            val roomId: String,
            val version: String
    )
}

internal class DefaultGetRoomSessionsDataTask @Inject constructor(
        private val roomKeysApi: RoomKeysApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetRoomSessionsDataTask {

    override suspend fun execute(params: GetRoomSessionsDataTask.Params): RoomKeysBackupData {
        return executeRequest(globalErrorReceiver) {
            roomKeysApi.getRoomSessionsData(
                    params.roomId,
                    params.version)
        }
    }
}
