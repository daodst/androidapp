

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoomJoinRulesAllowEntry(
        
        @Json(name = "room_id") val roomId: String?,
        
        @Json(name = "type") val type: String?
) {
    companion object {
        fun restrictedToRoom(roomId: String) = RoomJoinRulesAllowEntry(roomId, "m.room_membership")
    }
}
