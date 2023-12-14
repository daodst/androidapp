

package org.matrix.android.sdk.internal.session.search.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SearchResponse(
        
        @Json(name = "search_categories")
        val searchCategories: SearchResponseCategories
)
