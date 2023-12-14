

package org.matrix.android.sdk.internal.crypto

import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_OLM

internal object MXCryptoAlgorithms {

    
    fun hasEncryptorClassForAlgorithm(algorithm: String?): Boolean {
        return when (algorithm) {
            MXCRYPTO_ALGORITHM_MEGOLM,
            MXCRYPTO_ALGORITHM_OLM -> true
            else                   -> false
        }
    }

    

    fun hasDecryptorClassForAlgorithm(algorithm: String?): Boolean {
        return when (algorithm) {
            MXCRYPTO_ALGORITHM_MEGOLM,
            MXCRYPTO_ALGORITHM_OLM -> true
            else                   -> false
        }
    }

    
    fun supportedAlgorithms(): List<String> {
        return listOf(MXCRYPTO_ALGORITHM_MEGOLM, MXCRYPTO_ALGORITHM_OLM)
    }
}
