
package org.matrix.android.sdk.internal.session.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class DeviceInfo(
        
        @Json(name = "user_id")
        val userId: String? = null,

        
        @Json(name = "device_id")
        val deviceId: String? = null,

        
        @Json(name = "display_name")
        val displayName: String? = null,

        
        @Json(name = "last_seen_ts")
        val lastSeenTs: Long = 0,

        
        @Json(name = "last_seen_ip")
        val lastSeenIp: String? = null
)
