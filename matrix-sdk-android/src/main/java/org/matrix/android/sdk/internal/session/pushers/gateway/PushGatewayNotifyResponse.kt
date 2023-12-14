

package org.matrix.android.sdk.internal.session.pushers.gateway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PushGatewayNotifyResponse(
        @Json(name = "rejected")
        val rejectedPushKeys: List<String>
)
