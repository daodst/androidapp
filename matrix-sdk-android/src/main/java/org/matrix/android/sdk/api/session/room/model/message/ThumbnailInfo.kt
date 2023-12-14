

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ThumbnailInfo(
        
        @Json(name = "w") val width: Int = 0,

        
        @Json(name = "h") val height: Int = 0,

        
        @Json(name = "size") val size: Long = 0,

        
        @Json(name = "mimetype") val mimeType: String?
)
