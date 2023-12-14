
package org.matrix.android.sdk.internal.crypto.verification

internal interface VerificationInfoAccept : VerificationInfo<ValidVerificationInfoAccept> {
    
    val keyAgreementProtocol: String?

    
    val hash: String?

    
    val messageAuthenticationCode: String?

    
    val shortAuthenticationStrings: List<String>?

    
    var commitment: String?

    override fun asValidObject(): ValidVerificationInfoAccept? {
        val validTransactionId = transactionId?.takeIf { it.isNotEmpty() } ?: return null
        val validKeyAgreementProtocol = keyAgreementProtocol?.takeIf { it.isNotEmpty() } ?: return null
        val validHash = hash?.takeIf { it.isNotEmpty() } ?: return null
        val validMessageAuthenticationCode = messageAuthenticationCode?.takeIf { it.isNotEmpty() } ?: return null
        val validShortAuthenticationStrings = shortAuthenticationStrings?.takeIf { it.isNotEmpty() } ?: return null
        val validCommitment = commitment?.takeIf { it.isNotEmpty() } ?: return null

        return ValidVerificationInfoAccept(
                validTransactionId,
                validKeyAgreementProtocol,
                validHash,
                validMessageAuthenticationCode,
                validShortAuthenticationStrings,
                validCommitment
        )
    }
}

internal interface VerificationInfoAcceptFactory {

    fun create(tid: String,
               keyAgreementProtocol: String,
               hash: String,
               commitment: String,
               messageAuthenticationCode: String,
               shortAuthenticationStrings: List<String>): VerificationInfoAccept
}

internal data class ValidVerificationInfoAccept(
        val transactionId: String,
        val keyAgreementProtocol: String,
        val hash: String,
        val messageAuthenticationCode: String,
        val shortAuthenticationStrings: List<String>,
        var commitment: String?
)
