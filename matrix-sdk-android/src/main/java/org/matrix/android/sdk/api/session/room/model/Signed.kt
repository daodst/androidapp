

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Signed(
        @Json(name = "token") val token: String,
        @Json(name = "signatures") val signatures: Any,
        @Json(name = "mxid") val mxid: String
)
