

package im.vector.app.features.settings.notifications

import org.matrix.android.sdk.api.pushrules.rest.PushRule
import org.matrix.android.sdk.api.pushrules.toJson

enum class NotificationIndex {
    OFF,
    SILENT,
    NOISY;
}


val PushRule.notificationIndex: NotificationIndex?
    get() =
        NotificationIndex.values().firstOrNull {
            
            val standardAction = getStandardAction(this.ruleId, it) ?: return@firstOrNull false
            val indexActions = standardAction.actions ?: listOf()
            
            val targetRule = this.copy(enabled = standardAction != StandardActions.Disabled, actions = indexActions.toJson())
            ruleMatches(this, targetRule)
        }


private fun ruleMatches(rule: PushRule, targetRule: PushRule): Boolean {
    
    return (!rule.enabled && !targetRule.enabled) ||
            (rule.enabled &&
                    targetRule.enabled &&
                    rule.getHighlight() == targetRule.getHighlight() &&
                    rule.getNotificationSound() == targetRule.getNotificationSound() &&
                    rule.shouldNotify() == targetRule.shouldNotify() &&
                    rule.shouldNotNotify() == targetRule.shouldNotNotify())
}
