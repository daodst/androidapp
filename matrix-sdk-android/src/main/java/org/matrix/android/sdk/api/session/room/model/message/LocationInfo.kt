

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationInfo(
        
        @Json(name = "uri") val geoUri: String? = null,

        
        @Json(name = "description") val description: String? = null
)
