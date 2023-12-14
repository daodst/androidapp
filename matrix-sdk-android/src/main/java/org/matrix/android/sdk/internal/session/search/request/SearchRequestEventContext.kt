

package org.matrix.android.sdk.internal.session.search.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SearchRequestEventContext(
        
        @Json(name = "before_limit")
        val beforeLimit: Int? = null,
        
        @Json(name = "after_limit")
        val afterLimit: Int? = null,
        
        @Json(name = "include_profile")
        val includeProfile: Boolean? = null
)
