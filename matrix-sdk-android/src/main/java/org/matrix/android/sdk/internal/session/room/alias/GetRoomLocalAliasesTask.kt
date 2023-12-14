

package org.matrix.android.sdk.internal.session.room.alias

import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetRoomLocalAliasesTask : Task<GetRoomLocalAliasesTask.Params, List<String>> {
    data class Params(
            val roomId: String
    )
}

internal class DefaultGetRoomLocalAliasesTask @Inject constructor(
        private val roomAPI: RoomAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetRoomLocalAliasesTask {

    override suspend fun execute(params: GetRoomLocalAliasesTask.Params): List<String> {
        
        val response = executeRequest(globalErrorReceiver) {
            roomAPI.getAliases(roomId = params.roomId)
        }

        return response.aliases
    }
}
