
package org.matrix.android.sdk.api.pushrules

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.pushrules.rest.PushRule
import org.matrix.android.sdk.api.pushrules.rest.RuleSet
import org.matrix.android.sdk.api.session.events.model.Event

interface PushRuleService {
    
    fun fetchPushRules(scope: String = RuleScope.GLOBAL)

    fun getPushRules(scope: String = RuleScope.GLOBAL): RuleSet

    suspend fun updatePushRuleEnableStatus(kind: RuleKind, pushRule: PushRule, enabled: Boolean)

    suspend fun addPushRule(kind: RuleKind, pushRule: PushRule)

    

    suspend fun updatePushRuleActions(kind: RuleKind, ruleId: String, enable: Boolean, actions: List<Action>?)

    suspend fun removePushRule(kind: RuleKind, ruleId: String)

    fun addPushRuleListener(listener: PushRuleListener)

    fun removePushRuleListener(listener: PushRuleListener)

    fun getActions(event: Event): List<Action>


    fun resolveSenderNotificationPermissionCondition(event: Event,
                                                     condition: SenderNotificationPermissionCondition): Boolean

    interface PushRuleListener {
        fun onEvents(pushEvents: PushEvents)
    }

    fun getKeywords(): LiveData<Set<String>>
}
