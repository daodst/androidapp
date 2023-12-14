
package org.matrix.android.sdk.api.session.room.model.create

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Predecessor(
        @Json(name = "room_id") val roomId: String? = null,
        @Json(name = "event_id") val eventId: String? = null
)
