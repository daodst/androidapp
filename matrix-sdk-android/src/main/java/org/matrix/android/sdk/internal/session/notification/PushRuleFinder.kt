

package org.matrix.android.sdk.internal.session.notification

import org.matrix.android.sdk.api.pushrules.ConditionResolver
import org.matrix.android.sdk.api.pushrules.rest.PushRule
import org.matrix.android.sdk.api.session.events.model.Event
import javax.inject.Inject

internal class PushRuleFinder @Inject constructor(
        private val conditionResolver: ConditionResolver
) {
    fun fulfilledBingRule(event: Event, rules: List<PushRule>): PushRule? {
        return rules.firstOrNull { rule ->
            
            rule.enabled && rule.conditions?.all {
                it.asExecutableCondition(rule)?.isSatisfied(event, conditionResolver) ?: false
            } ?: false
        }
    }
}
