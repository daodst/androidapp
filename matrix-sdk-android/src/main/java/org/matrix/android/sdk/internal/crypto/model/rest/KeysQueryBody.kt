

package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class KeysQueryBody(
        
        @Json(name = "timeout")
        val timeout: Int? = null,

        
        @Json(name = "device_keys")
        val deviceKeys: Map<String, List<String>>,

        
        @Json(name = "token")
        val token: String? = null
)
