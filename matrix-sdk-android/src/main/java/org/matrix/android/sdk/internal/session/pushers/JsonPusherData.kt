
package org.matrix.android.sdk.internal.session.pushers

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class JsonPusherData(
        
        @Json(name = "url")
        val url: String? = null,

        
        @Json(name = "format")
        val format: String? = null,

        @Json(name = "brand")
        val brand: String? = null
)
