
package org.matrix.android.sdk.internal.session.pushers

import org.matrix.android.sdk.api.pushrules.RuleKind
import org.matrix.android.sdk.api.pushrules.rest.PushRule
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface UpdatePushRuleEnableStatusTask : Task<UpdatePushRuleEnableStatusTask.Params, Unit> {
    data class Params(val kind: RuleKind,
                      val pushRule: PushRule,
                      val enabled: Boolean)
}

internal class DefaultUpdatePushRuleEnableStatusTask @Inject constructor(
        private val pushRulesApi: PushRulesApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : UpdatePushRuleEnableStatusTask {

    override suspend fun execute(params: UpdatePushRuleEnableStatusTask.Params) {
        return executeRequest(globalErrorReceiver) {
            pushRulesApi.updateEnableRuleStatus(
                    params.kind.value,
                    params.pushRule.ruleId,
                    EnabledBody(params.enabled)
            )
        }
    }
}
