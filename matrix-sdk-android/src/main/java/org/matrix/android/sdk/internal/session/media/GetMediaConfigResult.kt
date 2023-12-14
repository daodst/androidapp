

package org.matrix.android.sdk.internal.session.media

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetMediaConfigResult(
        
        @Json(name = "m.upload.size")
        val maxUploadSize: Long? = null
)
