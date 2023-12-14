

package org.matrix.android.sdk.internal.session.user.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SearchUser(
        @Json(name = "user_id") val userId: String,
        @Json(name = "display_name") val displayName: String? = null,
        @Json(name = "avatar_url") val avatarUrl: String? = null,
        @Json(name = "tel_numbers") val tel_numbers: List<String>? = null
)
