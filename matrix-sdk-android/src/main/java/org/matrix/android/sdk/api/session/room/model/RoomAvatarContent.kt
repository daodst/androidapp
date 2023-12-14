

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RoomAvatarContent(
        @Json(name = "url") val avatarUrl: String? = null
)
