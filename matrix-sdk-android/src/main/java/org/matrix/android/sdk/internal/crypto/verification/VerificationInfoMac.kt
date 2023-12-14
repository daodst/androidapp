
package org.matrix.android.sdk.internal.crypto.verification

internal interface VerificationInfoMac : VerificationInfo<ValidVerificationInfoMac> {
    
    val mac: Map<String, String>?

    
    val keys: String?

    override fun asValidObject(): ValidVerificationInfoMac? {
        val validTransactionId = transactionId?.takeIf { it.isNotEmpty() } ?: return null
        val validMac = mac?.takeIf { it.isNotEmpty() } ?: return null
        val validKeys = keys?.takeIf { it.isNotEmpty() } ?: return null

        return ValidVerificationInfoMac(
                validTransactionId,
                validMac,
                validKeys
        )
    }
}

internal interface VerificationInfoMacFactory {
    fun create(tid: String, mac: Map<String, String>, keys: String): VerificationInfoMac
}

internal data class ValidVerificationInfoMac(
        val transactionId: String,
        val mac: Map<String, String>,
        val keys: String
)
