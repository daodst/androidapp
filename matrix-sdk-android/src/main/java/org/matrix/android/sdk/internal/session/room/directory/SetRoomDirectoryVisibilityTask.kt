

package org.matrix.android.sdk.internal.session.room.directory

import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.directory.DirectoryAPI
import org.matrix.android.sdk.internal.session.directory.RoomDirectoryVisibilityJson
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface SetRoomDirectoryVisibilityTask : Task<SetRoomDirectoryVisibilityTask.Params, Unit> {
    data class Params(
            val roomId: String,
            val roomDirectoryVisibility: RoomDirectoryVisibility
    )
}

internal class DefaultSetRoomDirectoryVisibilityTask @Inject constructor(
        private val directoryAPI: DirectoryAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : SetRoomDirectoryVisibilityTask {

    override suspend fun execute(params: SetRoomDirectoryVisibilityTask.Params) {
        executeRequest(globalErrorReceiver) {
            directoryAPI.setRoomDirectoryVisibility(
                    params.roomId,
                    RoomDirectoryVisibilityJson(visibility = params.roomDirectoryVisibility)
            )
        }
    }
}
