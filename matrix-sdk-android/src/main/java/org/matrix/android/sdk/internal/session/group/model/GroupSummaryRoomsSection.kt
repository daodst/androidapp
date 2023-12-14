

package org.matrix.android.sdk.internal.session.group.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class GroupSummaryRoomsSection(

        @Json(name = "total_room_count_estimate") val totalRoomCountEstimate: Int? = null,

        @Json(name = "rooms") val rooms: List<String> = emptyList()

        
        
)
