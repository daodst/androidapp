

package org.matrix.android.sdk.internal.session.profile

import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.identity.toMedium
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal abstract class DeleteThreePidTask : Task<DeleteThreePidTask.Params, Unit> {
    data class Params(
            val threePid: ThreePid
    )
}

internal class DefaultDeleteThreePidTask @Inject constructor(
        private val profileAPI: ProfileAPI,
        private val globalErrorReceiver: GlobalErrorReceiver) : DeleteThreePidTask() {

    override suspend fun execute(params: Params) {
        val body = DeleteThreePidBody(
                medium = params.threePid.toMedium(),
                address = params.threePid.value
        )
        executeRequest(globalErrorReceiver) {
            profileAPI.deleteThreePid(body)
        }

        
    }
}
