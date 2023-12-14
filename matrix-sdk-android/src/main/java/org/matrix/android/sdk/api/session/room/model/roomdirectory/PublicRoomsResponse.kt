
package org.matrix.android.sdk.api.session.room.model.roomdirectory

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PublicRoomsResponse(
        
        @Json(name = "next_batch")
        val nextBatch: String? = null,

        
        @Json(name = "prev_batch")
        val prevBatch: String? = null,

        
        @Json(name = "chunk")
        val chunk: List<PublicRoom>? = null,

        
        @Json(name = "total_room_count_estimate")
        val totalRoomCountEstimate: Int? = null
)
