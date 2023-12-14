
package org.matrix.android.sdk.api.pushrules

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.internal.util.caseInsensitiveFind

class ContainsDisplayNameCondition : Condition {

    override fun isSatisfied(event: Event, conditionResolver: ConditionResolver): Boolean {
        return conditionResolver.resolveContainsDisplayNameCondition(event, this)
    }

    override fun technicalDescription() = "User is mentioned"

    fun isSatisfied(event: Event, displayName: String): Boolean {
        val message = when (event.type) {
            EventType.MESSAGE -> {
                event.content.toModel<MessageContent>()
            }
            
            
            
            
            
            else              -> null
        } ?: return false

        return message.body.caseInsensitiveFind(displayName)
    }
}
