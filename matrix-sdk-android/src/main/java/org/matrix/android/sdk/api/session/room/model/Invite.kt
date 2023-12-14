

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Invite(
        @Json(name = "display_name") val displayName: String,
        @Json(name = "signed") val signed: Signed

)
