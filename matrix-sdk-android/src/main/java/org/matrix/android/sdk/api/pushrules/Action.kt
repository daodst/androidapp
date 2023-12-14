
package org.matrix.android.sdk.api.pushrules

import org.matrix.android.sdk.api.pushrules.rest.PushRule
import timber.log.Timber

sealed class Action {
    object Notify : Action()
    object DoNotNotify : Action()
    data class Sound(val sound: String = ACTION_OBJECT_VALUE_VALUE_DEFAULT) : Action()
    data class Highlight(val highlight: Boolean) : Action()

    companion object {
        const val ACTION_NOTIFY = "notify"
        const val ACTION_DONT_NOTIFY = "dont_notify"
        const val ACTION_COALESCE = "coalesce"

        
        const val ACTION_OBJECT_SET_TWEAK_KEY = "set_tweak"

        const val ACTION_OBJECT_SET_TWEAK_VALUE_SOUND = "sound"
        const val ACTION_OBJECT_SET_TWEAK_VALUE_HIGHLIGHT = "highlight"

        const val ACTION_OBJECT_VALUE_KEY = "value"
        const val ACTION_OBJECT_VALUE_VALUE_DEFAULT = "default"
        const val ACTION_OBJECT_VALUE_VALUE_RING = "ring"
    }
}



@Suppress("IMPLICIT_CAST_TO_ANY")
fun List<Action>.toJson(): List<Any> {
    return map { action ->
        when (action) {
            is Action.Notify      -> Action.ACTION_NOTIFY
            is Action.DoNotNotify -> Action.ACTION_DONT_NOTIFY
            is Action.Sound       -> {
                mapOf(
                        Action.ACTION_OBJECT_SET_TWEAK_KEY to Action.ACTION_OBJECT_SET_TWEAK_VALUE_SOUND,
                        Action.ACTION_OBJECT_VALUE_KEY to action.sound
                )
            }
            is Action.Highlight   -> {
                mapOf(
                        Action.ACTION_OBJECT_SET_TWEAK_KEY to Action.ACTION_OBJECT_SET_TWEAK_VALUE_HIGHLIGHT,
                        Action.ACTION_OBJECT_VALUE_KEY to action.highlight
                )
            }
        }
    }
}

fun PushRule.getActions(): List<Action> {
    val result = ArrayList<Action>()

    actions.forEach { actionStrOrObj ->
        when (actionStrOrObj) {
            Action.ACTION_NOTIFY      -> Action.Notify
            Action.ACTION_DONT_NOTIFY -> Action.DoNotNotify
            is Map<*, *>              -> {
                when (actionStrOrObj[Action.ACTION_OBJECT_SET_TWEAK_KEY]) {
                    Action.ACTION_OBJECT_SET_TWEAK_VALUE_SOUND     -> {
                        (actionStrOrObj[Action.ACTION_OBJECT_VALUE_KEY] as? String)?.let { stringValue ->
                            Action.Sound(stringValue)
                        }
                        
                                ?: Action.Sound(Action.ACTION_OBJECT_VALUE_VALUE_DEFAULT)
                    }
                    Action.ACTION_OBJECT_SET_TWEAK_VALUE_HIGHLIGHT -> {
                        (actionStrOrObj[Action.ACTION_OBJECT_VALUE_KEY] as? Boolean)?.let { boolValue ->
                            Action.Highlight(boolValue)
                        }
                        
                                ?: Action.Highlight(true)
                    }
                    else                                           -> {
                        Timber.w("Unsupported set_tweak value ${actionStrOrObj[Action.ACTION_OBJECT_SET_TWEAK_KEY]}")
                        null
                    }
                }
            }
            else                      -> {
                Timber.w("Unsupported action type $actionStrOrObj")
                null
            }
        }?.let {
            result.add(it)
        }
    }

    return result
}
