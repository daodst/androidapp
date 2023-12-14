
package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.crosssigning.CryptoCrossSigningKey
import org.matrix.android.sdk.internal.crypto.model.CryptoInfoMapper

@JsonClass(generateAdapter = true)
internal data class RestKeyInfo(
        
        @Json(name = "user_id")
        val userId: String,

        
        @Json(name = "usage")
        val usages: List<String>?,

        
        @Json(name = "keys")
        val keys: Map<String, String>?,

        
        @Json(name = "signatures")
        val signatures: Map<String, Map<String, String>>? = null
) {
    fun toCryptoModel(): CryptoCrossSigningKey {
        return CryptoInfoMapper.map(this)
    }
}
