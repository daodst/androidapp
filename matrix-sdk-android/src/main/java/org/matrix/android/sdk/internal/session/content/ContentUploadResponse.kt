

package org.matrix.android.sdk.internal.session.content

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ContentUploadResponse(
        
        @Json(name = "content_uri") val contentUri: String
)
