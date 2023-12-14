

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.room.powerlevels.Role


@JsonClass(generateAdapter = true)
data class PowerLevelsContent(
        
        @Json(name = "ban") val ban: Int? = null,
        
        @Json(name = "kick") val kick: Int? = null,
        
        @Json(name = "invite") val invite: Int? = null,
        
        @Json(name = "redact") val redact: Int? = null,
        
        @Json(name = "events_default") val eventsDefault: Int? = null,
        
        @Json(name = "events") val events: Map<String, Int>? = null,
        
        @Json(name = "users_default") val usersDefault: Int? = null,
        
        @Json(name = "users") val users: Map<String, Int>? = null,
        
        @Json(name = "state_default") val stateDefault: Int? = null,
        
        @Json(name = "notifications") val notifications: Map<String, Any>? = null
) {
    
    fun setUserPowerLevel(userId: String, powerLevel: Int?): PowerLevelsContent {
        return copy(
                users = users.orEmpty().toMutableMap().apply {
                    if (powerLevel == null || powerLevel == usersDefault) {
                        remove(userId)
                    } else {
                        put(userId, powerLevel)
                    }
                }
        )
    }

    
    fun notificationLevel(key: String): Int {
        return when (val value = notifications.orEmpty()[key]) {
            
            is String -> value.toInt()
            is Double -> value.toInt()
            is Int    -> value
            else      -> Role.Moderator.value
        }
    }

    companion object {
        
        const val NOTIFICATIONS_ROOM_KEY = "room"
    }
}

fun PowerLevelsContent.banOrDefault() = ban ?: Role.Moderator.value
fun PowerLevelsContent.kickOrDefault() = kick ?: Role.Moderator.value
fun PowerLevelsContent.inviteOrDefault() = invite ?: Role.Moderator.value
fun PowerLevelsContent.redactOrDefault() = redact ?: Role.Moderator.value
fun PowerLevelsContent.eventsDefaultOrDefault() = eventsDefault ?: Role.Default.value
fun PowerLevelsContent.usersDefaultOrDefault() = usersDefault ?: Role.Default.value
fun PowerLevelsContent.stateDefaultOrDefault() = stateDefault ?: Role.Moderator.value
