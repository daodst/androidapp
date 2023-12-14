

package org.matrix.android.sdk.internal.session.search.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SearchRequestFilter(
        
        @Json(name = "limit")
        val limit: Int? = null,
        
        @Json(name = "rooms")
        val rooms: List<String>? = null
)
