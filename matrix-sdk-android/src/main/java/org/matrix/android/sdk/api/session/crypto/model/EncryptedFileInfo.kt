

package org.matrix.android.sdk.api.session.crypto.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class EncryptedFileInfo(
        
        @Json(name = "url")
        val url: String? = null,

        
        @Json(name = "key")
        val key: EncryptedFileKey? = null,

        
        @Json(name = "iv")
        val iv: String? = null,

        
        @Json(name = "hashes")
        val hashes: Map<String, String>? = null,

        
        @Json(name = "v")
        val v: String? = null
) {
    
    fun isValid(): Boolean {
        if (url.isNullOrBlank()) {
            return false
        }

        if (key?.isValid() != true) {
            return false
        }

        if (iv.isNullOrBlank()) {
            return false
        }

        if (hashes?.containsKey("sha256") != true) {
            return false
        }

        if (v != "v2") {
            return false
        }

        return true
    }
}
