
package org.matrix.android.sdk.internal.crypto.tasks

import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface RedactEventTask : Task<RedactEventTask.Params, String> {
    data class Params(
            val txID: String,
            val roomId: String,
            val eventId: String,
            val reason: String?
    )
}

internal class DefaultRedactEventTask @Inject constructor(
        private val roomAPI: RoomAPI,
        private val globalErrorReceiver: GlobalErrorReceiver) : RedactEventTask {

    override suspend fun execute(params: RedactEventTask.Params): String {
        val response = executeRequest(globalErrorReceiver) {
            roomAPI.redactEvent(
                    txId = params.txID,
                    roomId = params.roomId,
                    eventId = params.eventId,
                    reason = if (params.reason == null) emptyMap() else mapOf("reason" to params.reason)
            )
        }
        return response.eventId
    }
}
