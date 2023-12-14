
package org.matrix.android.sdk.internal.session.pushers

import org.matrix.android.sdk.api.pushrules.RuleKind
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface RemovePushRuleTask : Task<RemovePushRuleTask.Params, Unit> {
    data class Params(
            val kind: RuleKind,
            val ruleId: String
    )
}

internal class DefaultRemovePushRuleTask @Inject constructor(
        private val pushRulesApi: PushRulesApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : RemovePushRuleTask {

    override suspend fun execute(params: RemovePushRuleTask.Params) {
        return executeRequest(globalErrorReceiver) {
            pushRulesApi.deleteRule(params.kind.value, params.ruleId)
        }
    }
}
