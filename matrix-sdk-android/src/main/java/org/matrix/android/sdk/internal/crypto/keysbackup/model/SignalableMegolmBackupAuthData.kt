

package org.matrix.android.sdk.internal.crypto.keysbackup.model

import org.matrix.android.sdk.api.util.JsonDict

internal data class SignalableMegolmBackupAuthData(
        val publicKey: String,
        val privateKeySalt: String? = null,
        val privateKeyIterations: Int? = null
) {
    fun signalableJSONDictionary(): JsonDict = HashMap<String, Any>().apply {
        put("public_key", publicKey)

        privateKeySalt?.let {
            put("private_key_salt", it)
        }
        privateKeyIterations?.let {
            put("private_key_iterations", it)
        }
    }
}
