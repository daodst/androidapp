
package org.matrix.android.sdk.api.pushrules

import org.matrix.android.sdk.api.session.events.model.Event

interface Condition {
    fun isSatisfied(event: Event, conditionResolver: ConditionResolver): Boolean

    fun technicalDescription(): String
}
