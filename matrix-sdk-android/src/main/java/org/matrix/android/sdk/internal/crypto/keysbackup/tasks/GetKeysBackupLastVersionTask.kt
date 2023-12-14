

package org.matrix.android.sdk.internal.crypto.keysbackup.tasks

import org.matrix.android.sdk.api.failure.is404
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupLastVersionResult
import org.matrix.android.sdk.internal.crypto.keysbackup.api.RoomKeysApi
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetKeysBackupLastVersionTask : Task<Unit, KeysBackupLastVersionResult>

internal class DefaultGetKeysBackupLastVersionTask @Inject constructor(
        private val roomKeysApi: RoomKeysApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetKeysBackupLastVersionTask {

    override suspend fun execute(params: Unit): KeysBackupLastVersionResult {
        return try {
            val keysVersionResult = executeRequest(globalErrorReceiver) {
                roomKeysApi.getKeysBackupLastVersion()
            }
            KeysBackupLastVersionResult.KeysBackup(keysVersionResult)
        } catch (throwable: Throwable) {
            if (throwable.is404()) {
                KeysBackupLastVersionResult.NoKeysBackup
            } else {
                
                throw throwable
            }
        }
    }
}
