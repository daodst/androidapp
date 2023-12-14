

package org.matrix.android.sdk.internal.session.search.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SearchResponseRoomEvents(
        
        @Json(name = "results")
        val results: List<SearchResponseItem>? = null,
        @Json(name = "count")
        val count: Int? = null,
        
        @Json(name = "highlights")
        val highlights: List<String>? = null,
        
        @Json(name = "next_batch")
        val nextBatch: String? = null
)
