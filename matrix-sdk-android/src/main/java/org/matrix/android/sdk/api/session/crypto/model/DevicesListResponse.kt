

package org.matrix.android.sdk.api.session.crypto.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DevicesListResponse(
        @Json(name = "devices")
        val devices: List<DeviceInfo>? = null
)
