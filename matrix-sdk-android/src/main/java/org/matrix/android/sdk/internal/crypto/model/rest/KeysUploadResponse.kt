
package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class KeysUploadResponse(
        
        @Json(name = "one_time_key_counts")
        val oneTimeKeyCounts: Map<String, Int>? = null
) {
    
    fun oneTimeKeyCountsForAlgorithm(algorithm: String): Int {
        return oneTimeKeyCounts?.get(algorithm) ?: 0
    }

    
    fun hasOneTimeKeyCountsForAlgorithm(algorithm: String): Boolean {
        return oneTimeKeyCounts?.containsKey(algorithm) == true
    }
}
