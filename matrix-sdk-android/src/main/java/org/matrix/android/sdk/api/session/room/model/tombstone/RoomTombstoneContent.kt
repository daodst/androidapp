
package org.matrix.android.sdk.api.session.room.model.tombstone

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RoomTombstoneContent(
        
        @Json(name = "body") val body: String? = null,

        
        @Json(name = "replacement_room") val replacementRoomId: String?
)
