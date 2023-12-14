

package org.matrix.android.sdk.internal.session.room.state

import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface SendStateTask : Task<SendStateTask.Params, Unit> {
    data class Params(
            val roomId: String,
            val stateKey: String,
            val eventType: String,
            val body: JsonDict
    )
}

internal class DefaultSendStateTask @Inject constructor(
        private val roomAPI: RoomAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : SendStateTask {

    override suspend fun execute(params: SendStateTask.Params) {
        return executeRequest(globalErrorReceiver) {
            if (params.stateKey.isEmpty()) {
                roomAPI.sendStateEvent(
                        roomId = params.roomId,
                        stateEventType = params.eventType,
                        params = params.body
                )
            } else {
                roomAPI.sendStateEvent(
                        roomId = params.roomId,
                        stateEventType = params.eventType,
                        stateKey = params.stateKey,
                        params = params.body
                )
            }
        }
    }
}
