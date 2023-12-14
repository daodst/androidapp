

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Event

@JsonClass(generateAdapter = true)
data class RoomSyncTimeline(

        
        @Json(name = "events") val events: List<Event>? = null,

        
        @Json(name = "limited") val limited: Boolean = false,

        
        @Json(name = "prev_batch") val prevToken: String? = null
)
