
package org.matrix.android.sdk.internal.database.mapper

import com.squareup.moshi.Types
import io.realm.RealmList
import org.matrix.android.sdk.api.pushrules.Kind
import org.matrix.android.sdk.api.pushrules.rest.PushCondition
import org.matrix.android.sdk.api.pushrules.rest.PushRule
import org.matrix.android.sdk.internal.database.model.PushRuleEntity
import org.matrix.android.sdk.internal.di.MoshiProvider
import timber.log.Timber

internal object PushRulesMapper {

    private val moshiActionsAdapter = MoshiProvider.providesMoshi().adapter<List<Any>>(Types.newParameterizedType(List::class.java, Any::class.java))


    fun mapContentRule(pushrule: PushRuleEntity): PushRule {
        return PushRule(
                actions = fromActionStr(pushrule.actionsStr),
                default = pushrule.default,
                enabled = pushrule.enabled,
                ruleId = pushrule.ruleId,
                conditions = listOf(
                        PushCondition(Kind.EventMatch.value, "content.body", pushrule.pattern)
                )
        )
    }

    private fun fromActionStr(actionsStr: String?): List<Any> {
        try {
            return actionsStr?.let { moshiActionsAdapter.fromJson(it) }.orEmpty()
        } catch (e: Throwable) {
            Timber.e(e, "## failed to map push rule actions <$actionsStr>")
            return emptyList()
        }
    }

    fun mapRoomRule(pushrule: PushRuleEntity): PushRule {
        return PushRule(
                actions = fromActionStr(pushrule.actionsStr),
                default = pushrule.default,
                enabled = pushrule.enabled,
                ruleId = pushrule.ruleId,
                conditions = listOf(
                        PushCondition(Kind.EventMatch.value, "room_id", pushrule.ruleId)
                )
        )
    }

    fun mapSenderRule(pushrule: PushRuleEntity): PushRule {
        return PushRule(
                actions = fromActionStr(pushrule.actionsStr),
                default = pushrule.default,
                enabled = pushrule.enabled,
                ruleId = pushrule.ruleId,
                conditions = listOf(
                        PushCondition(Kind.EventMatch.value, "user_id", pushrule.ruleId)
                )
        )
    }

    fun map(pushrule: PushRuleEntity): PushRule {
        return PushRule(
                actions = fromActionStr(pushrule.actionsStr),
                default = pushrule.default,
                enabled = pushrule.enabled,
                ruleId = pushrule.ruleId,
                conditions = pushrule.conditions?.map { PushConditionMapper.map(it) }
        )
    }

    fun map(pushRule: PushRule): PushRuleEntity {
        return PushRuleEntity(
                actionsStr = moshiActionsAdapter.toJson(pushRule.actions),
                default = pushRule.default ?: false,
                enabled = pushRule.enabled,
                ruleId = pushRule.ruleId,
                pattern = pushRule.pattern,
                conditions = pushRule.conditions?.let {
                    RealmList(*pushRule.conditions.map { PushConditionMapper.map(it) }.toTypedArray())
                } ?: RealmList()
        )
    }
}
