

package org.matrix.android.sdk.internal.session.room.create

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class CreateRoomResponse(
        
        @Json(name = "room_id") val roomId: String
)

internal typealias JoinRoomResponse = CreateRoomResponse
