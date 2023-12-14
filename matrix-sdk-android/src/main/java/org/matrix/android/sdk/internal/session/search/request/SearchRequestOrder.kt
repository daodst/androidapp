

package org.matrix.android.sdk.internal.session.search.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = false)
internal enum class SearchRequestOrder {
    @Json(name = "rank") RANK,
    @Json(name = "recent") RECENT
}
