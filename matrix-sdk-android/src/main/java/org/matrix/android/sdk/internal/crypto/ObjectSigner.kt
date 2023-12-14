

package org.matrix.android.sdk.internal.crypto

import org.matrix.android.sdk.api.auth.data.Credentials
import javax.inject.Inject

internal class ObjectSigner @Inject constructor(private val credentials: Credentials,
                                                private val olmDevice: MXOlmDevice) {

    
    fun signObject(strToSign: String): Map<String, Map<String, String>> {
        val result = HashMap<String, Map<String, String>>()

        val content = HashMap<String, String>()

        content["ed25519:" + credentials.deviceId] = olmDevice.signMessage(strToSign)
                ?: "" 

        result[credentials.userId] = content

        return result
    }
}
