

package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class KeysClaimResponse(
        
        @Json(name = "one_time_keys")
        val oneTimeKeys: Map<String, Map<String, Map<String, Map<String, Any>>>>? = null
)
