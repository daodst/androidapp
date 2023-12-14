

package org.matrix.android.sdk.api.session.crypto.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnsignedDeviceInfo(
        
        @Json(name = "device_display_name")
        val deviceDisplayName: String? = null
)
