

package org.matrix.android.sdk.api.pushrules.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.pushrules.Action
import org.matrix.android.sdk.api.pushrules.getActions
import org.matrix.android.sdk.api.pushrules.toJson


@JsonClass(generateAdapter = true)
data class PushRule(
        
        @Json(name = "actions")
        val actions: List<Any>,
        
        @Json(name = "default")
        val default: Boolean? = false,
        
        @Json(name = "enabled")
        val enabled: Boolean,
        
        @Json(name = "rule_id")
        val ruleId: String,
        
        @Json(name = "conditions")
        val conditions: List<PushCondition>? = null,
        
        @Json(name = "pattern")
        val pattern: String? = null
) {
    
    fun setNotificationSound(): PushRule {
        return setNotificationSound(Action.ACTION_OBJECT_VALUE_VALUE_DEFAULT)
    }

    fun getNotificationSound(): String? {
        return (getActions().firstOrNull { it is Action.Sound } as? Action.Sound)?.sound
    }

    
    fun setNotificationSound(sound: String): PushRule {
        return copy(
                actions = (getActions().filter { it !is Action.Sound } + Action.Sound(sound)).toJson()
        )
    }

    
    fun removeNotificationSound(): PushRule {
        return copy(
                actions = getActions().filter { it !is Action.Sound }.toJson()
        )
    }

    
    fun setHighlight(highlight: Boolean): PushRule {
        return copy(
                actions = (getActions().filter { it !is Action.Highlight } + Action.Highlight(highlight)).toJson()
        )
    }

    
    fun getHighlight(): Boolean {
        return getActions().filterIsInstance<Action.Highlight>().firstOrNull()?.highlight.orFalse()
    }

    
    fun setNotify(notify: Boolean): PushRule {
        val mutableActions = actions.toMutableList()

        mutableActions.remove(Action.ACTION_DONT_NOTIFY)
        mutableActions.remove(Action.ACTION_NOTIFY)

        if (notify) {
            mutableActions.add(Action.ACTION_NOTIFY)
        } else {
            mutableActions.add(Action.ACTION_DONT_NOTIFY)
        }

        return copy(actions = mutableActions)
    }

    
    fun shouldNotify() = actions.contains(Action.ACTION_NOTIFY)

    
    fun shouldNotNotify() = actions.contains(Action.ACTION_DONT_NOTIFY)
}
