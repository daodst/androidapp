

package org.matrix.android.sdk.api.session.room.model.relation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReplyToContent(
        @Json(name = "event_id") val eventId: String? = null
)
