

package org.matrix.android.sdk.internal.session.space

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SpacesResponse(
        
        @Json(name = "next_batch") val nextBatch: String? = null,
        
        @Json(name = "rooms") val rooms: List<SpaceChildSummaryResponse>? = null
)
