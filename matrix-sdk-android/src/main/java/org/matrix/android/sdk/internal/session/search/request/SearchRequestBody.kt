

package org.matrix.android.sdk.internal.session.search.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SearchRequestBody(
        
        @Json(name = "search_categories")
        val searchCategories: SearchRequestCategories
)
