

package org.matrix.android.sdk.internal.session.room

import org.matrix.android.sdk.api.session.room.model.RoomStrippedState
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetRoomSummaryTask : Task<GetRoomSummaryTask.Params, RoomStrippedState> {
    data class Params(
            val roomId: String,
            val viaServers: List<String>?
    )
}

internal class DefaultGetRoomSummaryTask @Inject constructor(
        private val roomAPI: RoomAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetRoomSummaryTask {

    override suspend fun execute(params: GetRoomSummaryTask.Params): RoomStrippedState {
        return executeRequest(globalErrorReceiver) {
            roomAPI.getRoomSummary(params.roomId, params.viaServers)
        }
    }
}
