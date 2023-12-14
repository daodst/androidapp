
package org.matrix.android.sdk.api.pushrules.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.pushrules.RuleIds
import org.matrix.android.sdk.api.pushrules.RuleSetKey


@JsonClass(generateAdapter = true)
data class RuleSet(
        @Json(name = "content")
        val content: List<PushRule>? = null,
        @Json(name = "override")
        val override: List<PushRule>? = null,
        @Json(name = "room")
        val room: List<PushRule>? = null,
        @Json(name = "sender")
        val sender: List<PushRule>? = null,
        @Json(name = "underride")
        val underride: List<PushRule>? = null
) {
    fun getAllRules(): List<PushRule> {
        
        return override.orEmpty() + content.orEmpty() + room.orEmpty() + sender.orEmpty() + underride.orEmpty()
    }

    
    fun findDefaultRule(ruleId: String?): PushRuleAndKind? {
        var result: PushRuleAndKind? = null
        
        if (null != ruleId) {
            if (RuleIds.RULE_ID_CONTAIN_USER_NAME == ruleId) {
                result = findRule(content, ruleId)?.let { PushRuleAndKind(it, RuleSetKey.CONTENT) }
            } else {
                
                result = findRule(override, ruleId)?.let { PushRuleAndKind(it, RuleSetKey.OVERRIDE) }
                if (null == result) {
                    result = findRule(underride, ruleId)?.let { PushRuleAndKind(it, RuleSetKey.UNDERRIDE) }
                }
            }
        }
        return result
    }

    
    private fun findRule(rules: List<PushRule>?, ruleId: String): PushRule? {
        return rules?.firstOrNull { it.ruleId == ruleId }
    }
}

data class PushRuleAndKind(
        val pushRule: PushRule,
        val kind: RuleSetKey
)
