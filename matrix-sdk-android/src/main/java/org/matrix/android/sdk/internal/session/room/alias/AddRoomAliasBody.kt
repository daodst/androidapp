

package org.matrix.android.sdk.internal.session.room.alias

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AddRoomAliasBody(
        
        @Json(name = "room_id") val roomId: String
)
