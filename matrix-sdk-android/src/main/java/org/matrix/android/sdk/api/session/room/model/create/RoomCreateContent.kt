

package org.matrix.android.sdk.api.session.room.model.create

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RoomCreateContent(
        @Json(name = "creator") val creator: String? = null,
        @Json(name = "room_version") val roomVersion: String? = null,
        @Json(name = "predecessor") val predecessor: Predecessor? = null,
        
        @Json(name = "type") val type: String? = null
)
