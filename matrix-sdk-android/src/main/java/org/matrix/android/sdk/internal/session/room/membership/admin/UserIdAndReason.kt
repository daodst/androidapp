
package org.matrix.android.sdk.internal.session.room.membership.admin

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UserIdAndReason(
        @Json(name = "user_id") val userId: String,
        @Json(name = "reason") val reason: String? = null
)
