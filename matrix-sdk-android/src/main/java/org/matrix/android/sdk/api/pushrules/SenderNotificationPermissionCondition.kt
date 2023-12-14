
package org.matrix.android.sdk.api.pushrules

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper

class SenderNotificationPermissionCondition(
        
        val key: String
) : Condition {

    override fun isSatisfied(event: Event, conditionResolver: ConditionResolver): Boolean {
        return conditionResolver.resolveSenderNotificationPermissionCondition(event, this)
    }

    override fun technicalDescription() = "User power level <$key>"

    fun isSatisfied(event: Event, powerLevels: PowerLevelsContent): Boolean {
        val powerLevelsHelper = PowerLevelsHelper(powerLevels)
        return event.senderId != null && powerLevelsHelper.getUserPowerLevelValue(event.senderId) >= powerLevels.notificationLevel(key)
    }
}
