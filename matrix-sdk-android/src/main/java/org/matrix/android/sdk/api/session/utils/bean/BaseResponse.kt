package org.matrix.android.sdk.api.session.utils.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class BaseResponse<T>(

        @Json(name = "status")
        val status: Int = 0,

        @Json(name = "info")
        val info: String = "",

        
        @Json(name = "data")
        val data: T
)
