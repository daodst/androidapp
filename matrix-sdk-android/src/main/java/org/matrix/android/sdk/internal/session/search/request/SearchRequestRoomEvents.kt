

package org.matrix.android.sdk.internal.session.search.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SearchRequestRoomEvents(
        
        @Json(name = "search_term")
        val searchTerm: String,

        
        @Json(name = "keys")
        val keys: Any? = null,

        
        @Json(name = "filter")
        val filter: SearchRequestFilter? = null,

        
        @Json(name = "order_by")
        val orderBy: SearchRequestOrder? = null,

        
        @Json(name = "event_context")
        val eventContext: SearchRequestEventContext? = null,

        
        @Json(name = "include_state")
        val include_state: Boolean? = null

        
        
)
