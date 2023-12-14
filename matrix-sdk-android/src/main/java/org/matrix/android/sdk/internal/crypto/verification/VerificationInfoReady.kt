
package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.api.session.crypto.verification.ValidVerificationInfoReady



internal interface VerificationInfoReady : VerificationInfo<ValidVerificationInfoReady> {
    
    val fromDevice: String?

    
    val methods: List<String>?

    override fun asValidObject(): ValidVerificationInfoReady? {
        val validTransactionId = transactionId?.takeIf { it.isNotEmpty() } ?: return null
        val validFromDevice = fromDevice?.takeIf { it.isNotEmpty() } ?: return null
        val validMethods = methods?.takeIf { it.isNotEmpty() } ?: return null

        return ValidVerificationInfoReady(
                validTransactionId,
                validFromDevice,
                validMethods
        )
    }
}

internal interface MessageVerificationReadyFactory {
    fun create(tid: String, methods: List<String>, fromDevice: String): VerificationInfoReady
}
