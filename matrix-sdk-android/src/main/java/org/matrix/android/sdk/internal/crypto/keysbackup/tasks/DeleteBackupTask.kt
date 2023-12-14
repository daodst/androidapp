

package org.matrix.android.sdk.internal.crypto.keysbackup.tasks

import org.matrix.android.sdk.internal.crypto.keysbackup.api.RoomKeysApi
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface DeleteBackupTask : Task<DeleteBackupTask.Params, Unit> {
    data class Params(
            val version: String
    )
}

internal class DefaultDeleteBackupTask @Inject constructor(
        private val roomKeysApi: RoomKeysApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : DeleteBackupTask {

    override suspend fun execute(params: DeleteBackupTask.Params) {
        return executeRequest(globalErrorReceiver) {
            roomKeysApi.deleteBackup(params.version)
        }
    }
}
