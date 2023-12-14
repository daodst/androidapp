

package org.matrix.android.sdk.internal.session.tts.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class TranslateResponse(
        @Json(name = "error") val error:Int,
        @Json(name = "text") val text: String
)
