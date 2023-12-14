
package org.matrix.android.sdk.internal.crypto.verification

internal interface VerificationInfoCancel : VerificationInfo<ValidVerificationInfoCancel> {
    
    val code: String?

    
    val reason: String?

    override fun asValidObject(): ValidVerificationInfoCancel? {
        val validTransactionId = transactionId?.takeIf { it.isNotEmpty() } ?: return null
        val validCode = code?.takeIf { it.isNotEmpty() } ?: return null

        return ValidVerificationInfoCancel(
                validTransactionId,
                validCode,
                reason
        )
    }
}

internal data class ValidVerificationInfoCancel(
        val transactionId: String,
        val code: String,
        val reason: String?
)
