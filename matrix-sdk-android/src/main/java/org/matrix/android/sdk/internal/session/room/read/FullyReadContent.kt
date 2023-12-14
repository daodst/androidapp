

package org.matrix.android.sdk.internal.session.room.read

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class FullyReadContent(
        @Json(name = "event_id") val eventId: String
)
