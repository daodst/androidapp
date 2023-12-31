

package org.matrix.android.sdk.internal.session.room.typing

import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface SendTypingTask : Task<SendTypingTask.Params, Unit> {

    data class Params(
            val roomId: String,
            val isTyping: Boolean,
            val typingTimeoutMillis: Int? = 30_000
    )
}

internal class DefaultSendTypingTask @Inject constructor(
        private val roomAPI: RoomAPI,
        @UserId private val userId: String,
        private val globalErrorReceiver: GlobalErrorReceiver
) : SendTypingTask {

    override suspend fun execute(params: SendTypingTask.Params) {
        executeRequest(globalErrorReceiver) {
            roomAPI.sendTypingState(
                    params.roomId,
                    userId,
                    TypingBody(params.isTyping, params.typingTimeoutMillis?.takeIf { params.isTyping })
            )
        }
    }
}
