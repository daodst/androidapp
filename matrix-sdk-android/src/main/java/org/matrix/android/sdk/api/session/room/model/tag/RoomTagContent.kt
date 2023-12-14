

package org.matrix.android.sdk.api.session.room.model.tag

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoomTagContent(
        @Json(name = "tags") val tags: Map<String, Map<String, Any>> = emptyMap()
)
