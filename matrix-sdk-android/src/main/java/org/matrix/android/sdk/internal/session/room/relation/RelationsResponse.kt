
package org.matrix.android.sdk.internal.session.room.relation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Event

@JsonClass(generateAdapter = true)
internal data class RelationsResponse(
        @Json(name = "chunk") val chunks: List<Event>,
        @Json(name = "original_event") val originalEvent: Event?,
        @Json(name = "next_batch") val nextBatch: String?,
        @Json(name = "prev_batch") val prevBatch: String?
)
