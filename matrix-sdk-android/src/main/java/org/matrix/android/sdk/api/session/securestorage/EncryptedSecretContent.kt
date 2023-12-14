

package org.matrix.android.sdk.api.session.securestorage

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.session.user.accountdata.AccountDataContent


@JsonClass(generateAdapter = true)
data class EncryptedSecretContent(
        
        @Json(name = "ciphertext") val ciphertext: String? = null,
        @Json(name = "mac") val mac: String? = null,
        @Json(name = "ephemeral") val ephemeral: String? = null,
        @Json(name = "iv") val initializationVector: String? = null
) : AccountDataContent {
    companion object {
        
        fun fromJson(obj: Any?): EncryptedSecretContent? {
            return MoshiProvider.providesMoshi()
                    .adapter(EncryptedSecretContent::class.java)
                    .fromJsonValue(obj)
        }
    }
}
