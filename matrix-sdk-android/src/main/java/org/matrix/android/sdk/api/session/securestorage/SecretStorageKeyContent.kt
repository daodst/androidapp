

package org.matrix.android.sdk.api.session.securestorage

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.util.JsonCanonicalizer



data class KeyInfo(
        val id: String,
        val content: SecretStorageKeyContent
)

@JsonClass(generateAdapter = true)
data class SecretStorageKeyContent(
        
        @Json(name = "algorithm") val algorithm: String? = null,
        @Json(name = "name") val name: String? = null,
        @Json(name = "passphrase") val passphrase: SsssPassphrase? = null,
        @Json(name = "pubkey") val publicKey: String? = null,
        @Json(name = "signatures") val signatures: Map<String, Map<String, String>>? = null
) {

    private fun signalableJSONDictionary(): Map<String, Any> {
        return mutableMapOf<String, Any>().apply {
            algorithm
                    ?.let { this["algorithm"] = it }
            name
                    ?.let { this["name"] = it }
            publicKey
                    ?.let { this["pubkey"] = it }
            passphrase
                    ?.let { ssssPassphrase ->
                        this["passphrase"] = mapOf(
                                "algorithm" to ssssPassphrase.algorithm,
                                "iterations" to ssssPassphrase.iterations,
                                "salt" to ssssPassphrase.salt
                        )
                    }
        }
    }

    fun canonicalSignable(): String {
        return JsonCanonicalizer.getCanonicalJson(Map::class.java, signalableJSONDictionary())
    }

    companion object {
        
        fun fromJson(obj: Any?): SecretStorageKeyContent? {
            return MoshiProvider.providesMoshi()
                    .adapter(SecretStorageKeyContent::class.java)
                    .fromJsonValue(obj)
        }
    }
}

@JsonClass(generateAdapter = true)
data class SsssPassphrase(
        @Json(name = "algorithm") val algorithm: String?,
        @Json(name = "iterations") val iterations: Int,
        @Json(name = "salt") val salt: String?
)
