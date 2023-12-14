
package org.matrix.android.sdk.internal.session.sync.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class DevicesListResponse(
        val devices: List<DeviceInfo>? = null
)
