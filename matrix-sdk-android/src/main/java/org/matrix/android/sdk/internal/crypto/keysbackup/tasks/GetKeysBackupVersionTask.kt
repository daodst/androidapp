

package org.matrix.android.sdk.internal.crypto.keysbackup.tasks

import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysVersionResult
import org.matrix.android.sdk.internal.crypto.keysbackup.api.RoomKeysApi
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetKeysBackupVersionTask : Task<String, KeysVersionResult>

internal class DefaultGetKeysBackupVersionTask @Inject constructor(
        private val roomKeysApi: RoomKeysApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetKeysBackupVersionTask {

    override suspend fun execute(params: String): KeysVersionResult {
        return executeRequest(globalErrorReceiver) {
            roomKeysApi.getKeysBackupVersion(params)
        }
    }
}
