package org.matrix.android.sdk.api.session.utils.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UserLevel(
        @Json(name = "pledge_level")
        val pledge_level: Int = -1
)
