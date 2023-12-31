

package org.matrix.android.sdk.internal.session.profile

import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal abstract class SetAvatarUrlTask : Task<SetAvatarUrlTask.Params, Unit> {
    data class Params(
            val userId: String,
            val newAvatarUrl: String
    )
}

internal class DefaultSetAvatarUrlTask @Inject constructor(
        private val profileAPI: ProfileAPI,
        private val globalErrorReceiver: GlobalErrorReceiver) : SetAvatarUrlTask() {

    override suspend fun execute(params: Params) {
        val body = SetAvatarUrlBody(
                avatarUrl = params.newAvatarUrl
        )
        return executeRequest(globalErrorReceiver) {
            profileAPI.setAvatarUrl(params.userId, body)
        }
    }
}
