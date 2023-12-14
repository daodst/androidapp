

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class RoomDirectoryVisibility {
    @Json(name = "private") PRIVATE,
    @Json(name = "public") PUBLIC
}
