

package org.matrix.android.sdk.internal.crypto.keysbackup.tasks

import org.matrix.android.sdk.internal.crypto.keysbackup.api.RoomKeysApi
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.UpdateKeysBackupVersionBody
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface UpdateKeysBackupVersionTask : Task<UpdateKeysBackupVersionTask.Params, Unit> {
    data class Params(
            val version: String,
            val keysBackupVersionBody: UpdateKeysBackupVersionBody
    )
}

internal class DefaultUpdateKeysBackupVersionTask @Inject constructor(
        private val roomKeysApi: RoomKeysApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : UpdateKeysBackupVersionTask {

    override suspend fun execute(params: UpdateKeysBackupVersionTask.Params) {
        return executeRequest(globalErrorReceiver) {
            roomKeysApi.updateKeysBackupVersion(params.version, params.keysBackupVersionBody)
        }
    }
}
