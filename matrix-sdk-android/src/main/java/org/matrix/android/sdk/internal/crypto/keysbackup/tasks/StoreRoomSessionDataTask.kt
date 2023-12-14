

package org.matrix.android.sdk.internal.crypto.keysbackup.tasks

import org.matrix.android.sdk.internal.crypto.keysbackup.api.RoomKeysApi
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.BackupKeysResult
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.KeyBackupData
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface StoreRoomSessionDataTask : Task<StoreRoomSessionDataTask.Params, BackupKeysResult> {
    data class Params(
            val roomId: String,
            val sessionId: String,
            val version: String,
            val keyBackupData: KeyBackupData
    )
}

internal class DefaultStoreRoomSessionDataTask @Inject constructor(
        private val roomKeysApi: RoomKeysApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : StoreRoomSessionDataTask {

    override suspend fun execute(params: StoreRoomSessionDataTask.Params): BackupKeysResult {
        return executeRequest(globalErrorReceiver) {
            roomKeysApi.storeRoomSessionData(
                    params.roomId,
                    params.sessionId,
                    params.version,
                    params.keyBackupData)
        }
    }
}
