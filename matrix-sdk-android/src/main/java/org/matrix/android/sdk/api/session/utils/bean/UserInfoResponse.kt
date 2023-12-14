package org.matrix.android.sdk.api.session.utils.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UserInfoResponse(

        @Json(name = "status")
        val status: Int = 0,

        @Json(name = "info")
        val info: String = "",

        
        @Json(name = "data")
        val data: UserInfoDataResponse?
)

@JsonClass(generateAdapter = true)
class UserInfoDataResponse(
        @Json(name = "from_address")
        var from_address: String? = null
)
