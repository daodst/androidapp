
package org.matrix.android.sdk.api.session.crypto.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.interfaces.DatedObject


@JsonClass(generateAdapter = true)
data class DeviceInfo(
        
        @Json(name = "user_id")
        val userId: String? = null,

        
        @Json(name = "device_id")
        val deviceId: String? = null,

        
        @Json(name = "display_name")
        val displayName: String? = null,

        
        @Json(name = "last_seen_ts")
        val lastSeenTs: Long? = null,

        
        @Json(name = "last_seen_ip")
        val lastSeenIp: String? = null
) : DatedObject {

    override val date: Long
        get() = lastSeenTs ?: 0
}
