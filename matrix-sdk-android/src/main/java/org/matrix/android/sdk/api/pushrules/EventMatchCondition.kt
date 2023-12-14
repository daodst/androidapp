
package org.matrix.android.sdk.api.pushrules

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.util.caseInsensitiveFind
import org.matrix.android.sdk.internal.util.hasSpecialGlobChar
import org.matrix.android.sdk.internal.util.simpleGlobToRegExp
import timber.log.Timber

class EventMatchCondition(
        
        val key: String,
        
        val pattern: String,
        
        val wordsOnly: Boolean
) : Condition {

    override fun isSatisfied(event: Event, conditionResolver: ConditionResolver): Boolean {
        return conditionResolver.resolveEventMatchCondition(event, this)
    }

    override fun technicalDescription() = "'$key' matches '$pattern', words only '$wordsOnly'"

    fun isSatisfied(event: Event): Boolean {
        
        val rawJson = MoshiProvider.providesMoshi().adapter(Event::class.java).toJsonValue(event) as? Map<*, *>
                ?: return false
        val value = extractField(rawJson, key) ?: return false

        
        
        return try {
            if (wordsOnly) {
                value.caseInsensitiveFind(pattern)
            } else {
                val modPattern = if (pattern.hasSpecialGlobChar()) {
                    
                    
                    pattern.removePrefix("*").removeSuffix("*").simpleGlobToRegExp()
                } else {
                    pattern.simpleGlobToRegExp()
                }
                val regex = Regex(modPattern, RegexOption.DOT_MATCHES_ALL)
                regex.containsMatchIn(value)
            }
        } catch (e: Throwable) {
            
            Timber.e(e, "Failed to evaluate push condition")
            false
        }
    }

    private fun extractField(jsonObject: Map<*, *>, fieldPath: String): String? {
        val fieldParts = fieldPath.split(".")
        if (fieldParts.isEmpty()) return null

        var jsonElement: Map<*, *> = jsonObject
        fieldParts.forEachIndexed { index, pathSegment ->
            if (index == fieldParts.lastIndex) {
                return jsonElement[pathSegment]?.toString()
            } else {
                val sub = jsonElement[pathSegment] ?: return null
                if (sub is Map<*, *>) {
                    jsonElement = sub
                } else {
                    return null
                }
            }
        }
        return null
    }
}
