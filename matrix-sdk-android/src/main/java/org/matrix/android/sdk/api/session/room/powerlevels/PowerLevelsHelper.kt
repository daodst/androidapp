

package org.matrix.android.sdk.api.session.room.powerlevels

import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.banOrDefault
import org.matrix.android.sdk.api.session.room.model.eventsDefaultOrDefault
import org.matrix.android.sdk.api.session.room.model.inviteOrDefault
import org.matrix.android.sdk.api.session.room.model.kickOrDefault
import org.matrix.android.sdk.api.session.room.model.redactOrDefault
import org.matrix.android.sdk.api.session.room.model.stateDefaultOrDefault
import org.matrix.android.sdk.api.session.room.model.usersDefaultOrDefault


class PowerLevelsHelper(private val powerLevelsContent: PowerLevelsContent) {

    
    fun getUserPowerLevelValue(userId: String): Int {
        return powerLevelsContent.users
                ?.get(userId)
                ?: powerLevelsContent.usersDefaultOrDefault()
    }

    
    fun getUserRole(userId: String): Role {
        val value = getUserPowerLevelValue(userId)
        
        return Role.fromValue(value, powerLevelsContent.eventsDefaultOrDefault())
    }

    
    fun isUserAllowedToSend(userId: String, isState: Boolean, eventType: String?): Boolean {
        return if (userId.isNotEmpty()) {
            val powerLevel = getUserPowerLevelValue(userId)
            val minimumPowerLevel = powerLevelsContent.events?.get(eventType)
                    ?: if (isState) {
                        powerLevelsContent.stateDefaultOrDefault()
                    } else {
                        powerLevelsContent.eventsDefaultOrDefault()
                    }
            powerLevel >= minimumPowerLevel
        } else false
    }

    
    fun isUserAbleToInvite(userId: String): Boolean {
        val powerLevel = getUserPowerLevelValue(userId)
        return powerLevel >= powerLevelsContent.inviteOrDefault()
    }

    
    fun isUserAbleToBan(userId: String): Boolean {
        val powerLevel = getUserPowerLevelValue(userId)
        return powerLevel >= powerLevelsContent.banOrDefault()
    }

    
    fun isUserAbleToKick(userId: String): Boolean {
        val powerLevel = getUserPowerLevelValue(userId)
        return powerLevel >= powerLevelsContent.kickOrDefault()
    }

    
    fun isUserAbleToRedact(userId: String): Boolean {
        val powerLevel = getUserPowerLevelValue(userId)
        return powerLevel >= powerLevelsContent.redactOrDefault()
    }
}
