package org.matrix.android.sdk.internal.session.sync.job.ws.pojo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
open class BaseResponse<T : Any>(

        @Json(name = "cmd")
        val cmd: String,

        @Json(name = "seq")
        val seq: Int,

        @Json(name = "version")
        val version: String,

        @Json(name = "position")
        val position: Int,

        @Json(name = "body")
        val body: T,
)

