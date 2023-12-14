

package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UpdateDeviceInfoBody(
        
        @Json(name = "display_name")
        val displayName: String? = null
)
