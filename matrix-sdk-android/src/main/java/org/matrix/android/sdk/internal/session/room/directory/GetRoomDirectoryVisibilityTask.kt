

package org.matrix.android.sdk.internal.session.room.directory

import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.directory.DirectoryAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetRoomDirectoryVisibilityTask : Task<GetRoomDirectoryVisibilityTask.Params, RoomDirectoryVisibility> {
    data class Params(
            val roomId: String
    )
}

internal class DefaultGetRoomDirectoryVisibilityTask @Inject constructor(
        private val directoryAPI: DirectoryAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetRoomDirectoryVisibilityTask {

    override suspend fun execute(params: GetRoomDirectoryVisibilityTask.Params): RoomDirectoryVisibility {
        return executeRequest(globalErrorReceiver) {
            directoryAPI.getRoomDirectoryVisibility(params.roomId)
        }
                .visibility
    }
}
