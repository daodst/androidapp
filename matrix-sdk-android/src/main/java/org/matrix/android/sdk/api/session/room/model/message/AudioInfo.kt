

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AudioInfo(
        
        @Json(name = "mimetype") val mimeType: String? = null,

        
        @Json(name = "size") val size: Long? = null,

        
        @Json(name = "duration") val duration: Int? = null,

        @Json(name = "originText") val originText: String? = null,

        @Json(name = "translate") val translate: String? = null

)
