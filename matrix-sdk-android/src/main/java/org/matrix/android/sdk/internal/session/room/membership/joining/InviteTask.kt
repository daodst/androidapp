

package org.matrix.android.sdk.internal.session.room.membership.joining

import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface InviteTask : Task<InviteTask.Params, Unit> {
    data class Params(
            val roomId: String,
            val userId: String,
            val reason: String?
    )
}

internal class DefaultInviteTask @Inject constructor(
        private val roomAPI: RoomAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : InviteTask {

    override suspend fun execute(params: InviteTask.Params) {
        val body = InviteBody(params.userId, params.reason)
        return executeRequest(
                globalErrorReceiver,
                canRetry = true,
                maxRetriesCount = 3
        ) {
            roomAPI.invite(params.roomId, body)
        }
    }
}
