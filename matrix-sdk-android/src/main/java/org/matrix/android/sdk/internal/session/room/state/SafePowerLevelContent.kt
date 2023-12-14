

package org.matrix.android.sdk.internal.session.room.state

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.util.JsonDict

@JsonClass(generateAdapter = true)
internal data class SerializablePowerLevelsContent(
        @Json(name = "ban") val ban: Int?,
        @Json(name = "kick") val kick: Int?,
        @Json(name = "invite") val invite: Int?,
        @Json(name = "redact") val redact: Int?,
        @Json(name = "events_default") val eventsDefault: Int?,
        @Json(name = "events") val events: Map<String, Int>?,
        @Json(name = "users_default") val usersDefault: Int?,
        @Json(name = "users") val users: Map<String, Int>?,
        @Json(name = "state_default") val stateDefault: Int?,
        
        @Json(name = "notifications") val notifications: Map<String, Int>?
)

internal fun JsonDict.toSafePowerLevelsContentDict(): JsonDict {
    return toModel<PowerLevelsContent>()
            ?.let { content ->
                SerializablePowerLevelsContent(
                        ban = content.ban,
                        kick = content.kick,
                        invite = content.invite,
                        redact = content.redact,
                        eventsDefault = content.eventsDefault,
                        events = content.events,
                        usersDefault = content.usersDefault,
                        users = content.users,
                        stateDefault = content.stateDefault,
                        notifications = content.notifications?.mapValues { content.notificationLevel(it.key) }
                )
            }
            ?.toContent()
            ?: emptyMap()
}
