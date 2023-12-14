

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoomsSyncResponse(
        
        @Json(name = "join") val join: Map<String, RoomSync> = emptyMap(),

        
        @Json(name = "invite") val invite: Map<String, InvitedRoomSync> = emptyMap(),

        
        @Json(name = "leave") val leave: Map<String, RoomSync> = emptyMap()
)
