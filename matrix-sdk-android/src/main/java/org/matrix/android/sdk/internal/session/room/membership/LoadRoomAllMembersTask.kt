

package org.matrix.android.sdk.internal.session.room.membership

import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.session.sync.SyncTokenStore
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface LoadRoomAllMembersTask : Task<LoadRoomAllMembersTask.Params, Map<String, Map<String, String>>> {

    data class Params(
            val roomId: String,
    )
}

internal class DefaultLoadRoomAllMembersTask @Inject constructor(
        private val roomAPI: RoomAPI,
        private val syncTokenStore: SyncTokenStore,
        private val globalErrorReceiver: GlobalErrorReceiver
) : LoadRoomAllMembersTask {

    override suspend fun execute(params: LoadRoomAllMembersTask.Params): Map<String, Map<String, String>> {
        val lastToken = syncTokenStore.getLastToken()
        val response = try {
            executeRequest(globalErrorReceiver) {
                roomAPI.getJoinedMembers(params.roomId, lastToken)
            }
        } catch (throwable: Throwable) {
            throw throwable
        }
      return  response.roomMemberEvents
    }
}
