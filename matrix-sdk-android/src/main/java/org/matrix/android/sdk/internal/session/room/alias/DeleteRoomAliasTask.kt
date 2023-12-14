

package org.matrix.android.sdk.internal.session.room.alias

import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.directory.DirectoryAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface DeleteRoomAliasTask : Task<DeleteRoomAliasTask.Params, Unit> {
    data class Params(
            val roomAlias: String
    )
}

internal class DefaultDeleteRoomAliasTask @Inject constructor(
        private val directoryAPI: DirectoryAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : DeleteRoomAliasTask {

    override suspend fun execute(params: DeleteRoomAliasTask.Params) {
        executeRequest(globalErrorReceiver) {
            directoryAPI.deleteRoomAlias(
                    roomAlias = params.roomAlias
            )
        }
    }
}
