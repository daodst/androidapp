

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DeviceListResponse(
        
        val changed: List<String> = emptyList(),
        
        val left: List<String> = emptyList()
)
