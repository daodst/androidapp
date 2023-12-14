
package org.matrix.android.sdk.internal.session.pushers

import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetPushRulesTask : Task<GetPushRulesTask.Params, Unit> {
    data class Params(val scope: String)
}


internal class DefaultGetPushRulesTask @Inject constructor(
        private val pushRulesApi: PushRulesApi,
        private val savePushRulesTask: SavePushRulesTask,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetPushRulesTask {

    override suspend fun execute(params: GetPushRulesTask.Params) {
        val response = executeRequest(globalErrorReceiver) {
            pushRulesApi.getAllRules()
        }

        savePushRulesTask.execute(SavePushRulesTask.Params(response))
    }
}
