

package org.matrix.android.sdk.internal.session.tts.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class TranslateStateResponse(
        @Json(name = "status")
        val status: Int = 0,

        @Json(name = "info")
        val info: String = "",

        
        @Json(name = "data")
        val data: TranslateStateResponseDat?
)

@JsonClass(generateAdapter = true)
class TranslateStateResponseDat(
        @Json(name = "ttts")
        var ttts: Boolean = false
)
