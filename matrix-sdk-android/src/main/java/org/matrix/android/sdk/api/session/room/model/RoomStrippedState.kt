
package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RoomStrippedState(
        
        @Json(name = "aliases")
        val aliases: List<String>? = null,

        
        @Json(name = "canonical_alias")
        val canonicalAlias: String? = null,

        
        @Json(name = "name")
        val name: String? = null,

        
        @Json(name = "num_joined_members")
        val numJoinedMembers: Int = 0,

        
        @Json(name = "room_id")
        val roomId: String,

        
        @Json(name = "topic")
        val topic: String? = null,

        
        @Json(name = "world_readable")
        val worldReadable: Boolean = false,

        
        @Json(name = "guest_can_join")
        val guestCanJoin: Boolean = false,

        
        @Json(name = "avatar_url")
        val avatarUrl: String? = null,

        
        @Json(name = "m.federate")
        val isFederated: Boolean = false,

        
        @Json(name = "is_encrypted")
        val isEncrypted: Boolean?,

        
        @Json(name = "room_type")
        val roomType: String?,

        
        @Json(name = "membership")
        val membership: String?
) {
    
    fun getPrimaryAlias(): String? {
        return canonicalAlias ?: aliases?.firstOrNull()
    }
}
