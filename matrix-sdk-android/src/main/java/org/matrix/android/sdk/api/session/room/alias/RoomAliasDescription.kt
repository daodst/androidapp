

package org.matrix.android.sdk.api.session.room.alias

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoomAliasDescription(
        
        @Json(name = "room_id") val roomId: String,

        
        @Json(name = "servers") val servers: List<String> = emptyList()
)
