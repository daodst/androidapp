

package org.matrix.android.sdk.api.session.crypto.keysbackup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.crypto.keysbackup.model.SignalableMegolmBackupAuthData
import org.matrix.android.sdk.internal.di.MoshiProvider


@JsonClass(generateAdapter = true)
data class MegolmBackupAuthData(
        
        @Json(name = "public_key")
        val publicKey: String,

        
        @Json(name = "private_key_salt")
        val privateKeySalt: String? = null,

        
        @Json(name = "private_key_iterations")
        val privateKeyIterations: Int? = null,

        
        @Json(name = "signatures")
        val signatures: Map<String, Map<String, String>>? = null
) {

    internal fun toJsonDict(): JsonDict {
        val moshi = MoshiProvider.providesMoshi()
        val adapter = moshi.adapter(Map::class.java)

        return moshi
                .adapter(MegolmBackupAuthData::class.java)
                .toJson(this)
                .let {
                    @Suppress("UNCHECKED_CAST")
                    adapter.fromJson(it) as JsonDict
                }
    }

    internal fun signalableJSONDictionary(): JsonDict {
        return SignalableMegolmBackupAuthData(
                publicKey = publicKey,
                privateKeySalt = privateKeySalt,
                privateKeyIterations = privateKeyIterations
        )
                .signalableJSONDictionary()
    }
}
