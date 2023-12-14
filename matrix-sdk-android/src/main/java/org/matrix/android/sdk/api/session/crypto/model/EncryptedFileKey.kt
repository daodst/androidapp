

package org.matrix.android.sdk.api.session.crypto.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EncryptedFileKey(
        
        @Json(name = "alg")
        val alg: String? = null,

        
        @Json(name = "ext")
        val ext: Boolean? = null,

        
        @Json(name = "key_ops")
        val keyOps: List<String>? = null,

        
        @Json(name = "kty")
        val kty: String? = null,

        
        @Json(name = "k")
        val k: String? = null
) {
    
    fun isValid(): Boolean {
        if (alg != "A256CTR") {
            return false
        }

        if (ext != true) {
            return false
        }

        if (keyOps?.contains("encrypt") != true || !keyOps.contains("decrypt")) {
            return false
        }

        if (kty != "oct") {
            return false
        }

        if (k.isNullOrBlank()) {
            return false
        }

        return true
    }
}
