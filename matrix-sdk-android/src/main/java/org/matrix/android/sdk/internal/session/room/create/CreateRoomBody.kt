

package org.matrix.android.sdk.internal.session.room.create

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset
import org.matrix.android.sdk.internal.session.room.membership.threepid.ThreePidInviteBody


@JsonClass(generateAdapter = true)
internal data class CreateRoomBody(
        
        @Json(name = "visibility")
        val visibility: RoomDirectoryVisibility?,

        
        @Json(name = "room_alias_name")
        val roomAliasName: String?,

        
        @Json(name = "name")
        val name: String?,

        
        @Json(name = "topic")
        val topic: String?,

        
        @Json(name = "invite")
        val invitedUserIds: List<String>?,

        
        @Json(name = "invite_3pid")
        val invite3pids: List<ThreePidInviteBody>?,

        
        @Json(name = "creation_content")
        val creationContent: Any?,

        
        @Json(name = "initial_state")
        val initialStates: List<Event>?,

        
        @Json(name = "preset")
        val preset: CreateRoomPreset?,

        
        @Json(name = "is_direct")
        val isDirect: Boolean?,

        
        @Json(name = "power_level_content_override")
        val powerLevelContentOverride: PowerLevelsContent?,

        
        @Json(name = "room_version")
        val roomVersion: String?
)
