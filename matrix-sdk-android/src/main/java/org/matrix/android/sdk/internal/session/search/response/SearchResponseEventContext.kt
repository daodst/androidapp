

package org.matrix.android.sdk.internal.session.search.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.util.JsonDict

@JsonClass(generateAdapter = true)
internal data class SearchResponseEventContext(
        
        @Json(name = "events_before")
        val eventsBefore: List<Event>,
        
        @Json(name = "events_after")
        val eventsAfter: List<Event>,
        
        @Json(name = "start")
        val start: String? = null,
        
        @Json(name = "end")
        val end: String? = null,
        
        @Json(name = "profile_info")
        val profileInfo: Map<String, JsonDict>? = null
)
