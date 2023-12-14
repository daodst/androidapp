

package im.vector.app.features.call.conference

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class JitsiWidgetData(
        @Json(name = "domain") val domain: String,
        @Json(name = "conferenceId") val confId: String,
        @Json(name = "isAudioOnly") val isAudioOnly: Boolean = false,
        @Json(name = "auth") val auth: String? = null
)
