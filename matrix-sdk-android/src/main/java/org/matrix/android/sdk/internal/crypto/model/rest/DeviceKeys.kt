

package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class DeviceKeys(
        
        @Json(name = "user_id")
        val userId: String,

        
        @Json(name = "device_id")
        val deviceId: String,

        
        @Json(name = "algorithms")
        val algorithms: List<String>?,

        
        @Json(name = "keys")
        val keys: Map<String, String>?,

        
        @Json(name = "signatures")
        val signatures: Map<String, Map<String, String>>?
)
