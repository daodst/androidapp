

package org.matrix.android.sdk.internal.session.pushers.gateway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PushGatewayNotification(
        @Json(name = "event_id")
        val eventId: String,

        
        @Json(name = "devices")
        val devices: List<PushGatewayDevice>
)
