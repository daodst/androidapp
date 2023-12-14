

package org.matrix.android.sdk.internal.session.pushers.gateway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PushGatewayDevice(
        
        @Json(name = "app_id")
        val appId: String,
        
        @Json(name = "pushkey")
        val pushKey: String
)
