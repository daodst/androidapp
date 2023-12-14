

package org.matrix.android.sdk.internal.session.room.tags

import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface DeleteTagFromRoomTask : Task<DeleteTagFromRoomTask.Params, Unit> {

    data class Params(
            val roomId: String,
            val tag: String
    )
}

internal class DefaultDeleteTagFromRoomTask @Inject constructor(
        private val roomAPI: RoomAPI,
        @UserId private val userId: String,
        private val globalErrorReceiver: GlobalErrorReceiver
) : DeleteTagFromRoomTask {

    override suspend fun execute(params: DeleteTagFromRoomTask.Params) {
        executeRequest(globalErrorReceiver) {
            roomAPI.deleteTag(
                    userId = userId,
                    roomId = params.roomId,
                    tag = params.tag
            )
        }
    }
}
