

package org.matrix.android.sdk.internal.session.group.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GroupUsers(
        @Json(name = "total_user_count_estimate") val totalUserCountEstimate: Int,
        @Json(name = "chunk") val users: List<GroupUser> = emptyList()
)
