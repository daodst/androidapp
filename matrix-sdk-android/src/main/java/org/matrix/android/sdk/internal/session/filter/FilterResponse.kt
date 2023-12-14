
package org.matrix.android.sdk.internal.session.filter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class FilterResponse(
        
        @Json(name = "filter_id") val filterId: String
)
