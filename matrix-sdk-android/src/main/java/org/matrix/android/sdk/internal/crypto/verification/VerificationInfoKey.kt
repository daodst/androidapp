
package org.matrix.android.sdk.internal.crypto.verification


internal interface VerificationInfoKey : VerificationInfo<ValidVerificationInfoKey> {
    
    val key: String?

    override fun asValidObject(): ValidVerificationInfoKey? {
        val validTransactionId = transactionId?.takeIf { it.isNotEmpty() } ?: return null
        val validKey = key?.takeIf { it.isNotEmpty() } ?: return null

        return ValidVerificationInfoKey(
                validTransactionId,
                validKey
        )
    }
}

internal interface VerificationInfoKeyFactory {
    fun create(tid: String, pubKey: String): VerificationInfoKey
}

internal data class ValidVerificationInfoKey(
        val transactionId: String,
        val key: String
)
