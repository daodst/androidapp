
package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class KeyChangesResponse(
        
        @Json(name = "changed")
        val changed: List<String>? = null,

        
        @Json(name = "left")
        val left: List<String>? = null
)
