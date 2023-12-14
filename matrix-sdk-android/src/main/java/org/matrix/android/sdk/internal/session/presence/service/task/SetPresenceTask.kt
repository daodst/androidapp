

package org.matrix.android.sdk.internal.session.presence.service.task

import org.matrix.android.sdk.api.session.presence.model.PresenceEnum
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.presence.PresenceAPI
import org.matrix.android.sdk.internal.session.presence.model.SetPresenceBody
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal abstract class SetPresenceTask : Task<SetPresenceTask.Params, Any> {
    data class Params(
            val userId: String,
            val presence: PresenceEnum,
            val statusMsg: String?
    )
}

internal class DefaultSetPresenceTask @Inject constructor(
        private val presenceAPI: PresenceAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : SetPresenceTask() {

    override suspend fun execute(params: Params): Any {
        return executeRequest(globalErrorReceiver) {
            val setPresenceBody = SetPresenceBody(params.presence, params.statusMsg)
            presenceAPI.setPresence(params.userId, setPresenceBody)
        }
    }
}
