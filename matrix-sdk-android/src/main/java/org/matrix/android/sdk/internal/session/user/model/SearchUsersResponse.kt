

package org.matrix.android.sdk.internal.session.user.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class SearchUsersResponse(
        @Json(name = "limited") val limited: Boolean = false,
        @Json(name = "results") val users: List<SearchUser> = emptyList()
)
