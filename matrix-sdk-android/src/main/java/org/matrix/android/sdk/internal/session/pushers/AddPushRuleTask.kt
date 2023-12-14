
package org.matrix.android.sdk.internal.session.pushers

import org.matrix.android.sdk.api.pushrules.RuleKind
import org.matrix.android.sdk.api.pushrules.rest.PushRule
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface AddPushRuleTask : Task<AddPushRuleTask.Params, Unit> {
    data class Params(
            val kind: RuleKind,
            val pushRule: PushRule
    )
}

internal class DefaultAddPushRuleTask @Inject constructor(
        private val pushRulesApi: PushRulesApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : AddPushRuleTask {

    override suspend fun execute(params: AddPushRuleTask.Params) {
        return executeRequest(globalErrorReceiver) {
            pushRulesApi.addRule(params.kind.value, params.pushRule.ruleId, params.pushRule)
        }
    }
}
