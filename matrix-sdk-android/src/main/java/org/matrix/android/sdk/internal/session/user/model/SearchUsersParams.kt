

package org.matrix.android.sdk.internal.session.user.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class SearchUsersParams(
        
        @Json(name = "search_term") val searchTerm: String,
        
        @Json(name = "limit") val limit: Int
)
