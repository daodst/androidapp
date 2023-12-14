
package org.matrix.android.sdk.api.session.events.model.content

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class SecretSendEventContent(
        @Json(name = "request_id") val requestId: String,
        @Json(name = "secret") val secretValue: String
)
