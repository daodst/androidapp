

package org.matrix.android.sdk.api.session.room.model.livelocation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BeaconInfo(
        @Json(name = "description") val description: String? = null,
        
        @Json(name = "timeout") val timeout: Long? = null,
        
        @Json(name = "live") val isLive: Boolean? = null
)
