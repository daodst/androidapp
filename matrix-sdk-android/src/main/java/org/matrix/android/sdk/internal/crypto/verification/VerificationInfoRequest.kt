
package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.api.session.crypto.verification.ValidVerificationInfoRequest

internal interface VerificationInfoRequest : VerificationInfo<ValidVerificationInfoRequest> {

    
    val fromDevice: String?

    
    val methods: List<String>?

    
    val timestamp: Long?

    override fun asValidObject(): ValidVerificationInfoRequest? {
        
        val validTransactionId = transactionId?.takeIf { it.isNotEmpty() } ?: return null
        val validFromDevice = fromDevice?.takeIf { it.isNotEmpty() } ?: return null
        val validMethods = methods?.takeIf { it.isNotEmpty() } ?: return null

        return ValidVerificationInfoRequest(
                validTransactionId,
                validFromDevice,
                validMethods,
                timestamp
        )
    }
}
