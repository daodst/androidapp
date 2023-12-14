
package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.api.session.crypto.verification.SasMode
import org.matrix.android.sdk.internal.crypto.model.rest.VERIFICATION_METHOD_RECIPROCATE
import org.matrix.android.sdk.internal.crypto.model.rest.VERIFICATION_METHOD_SAS

internal interface VerificationInfoStart : VerificationInfo<ValidVerificationInfoStart> {

    val method: String?

    
    val fromDevice: String?

    
    val keyAgreementProtocols: List<String>?

    
    val hashes: List<String>?

    
    val messageAuthenticationCodes: List<String>?

    
    val shortAuthenticationStrings: List<String>?

    
    val sharedSecret: String?

    fun toCanonicalJson(): String

    override fun asValidObject(): ValidVerificationInfoStart? {
        val validTransactionId = transactionId?.takeIf { it.isNotEmpty() } ?: return null
        val validFromDevice = fromDevice?.takeIf { it.isNotEmpty() } ?: return null

        return when (method) {
            VERIFICATION_METHOD_SAS         -> {
                val validKeyAgreementProtocols = keyAgreementProtocols?.takeIf { it.isNotEmpty() } ?: return null
                val validHashes = hashes?.takeIf { it.contains("sha256") } ?: return null
                val validMessageAuthenticationCodes = messageAuthenticationCodes
                        ?.takeIf {
                            it.contains(SASDefaultVerificationTransaction.SAS_MAC_SHA256) ||
                                    it.contains(SASDefaultVerificationTransaction.SAS_MAC_SHA256_LONGKDF)
                        }
                        ?: return null
                val validShortAuthenticationStrings = shortAuthenticationStrings?.takeIf { it.contains(SasMode.DECIMAL) } ?: return null

                ValidVerificationInfoStart.SasVerificationInfoStart(
                        validTransactionId,
                        validFromDevice,
                        validKeyAgreementProtocols,
                        validHashes,
                        validMessageAuthenticationCodes,
                        validShortAuthenticationStrings,
                        canonicalJson = toCanonicalJson()
                )
            }
            VERIFICATION_METHOD_RECIPROCATE -> {
                val validSharedSecret = sharedSecret?.takeIf { it.isNotEmpty() } ?: return null

                ValidVerificationInfoStart.ReciprocateVerificationInfoStart(
                        validTransactionId,
                        validFromDevice,
                        validSharedSecret
                )
            }
            else                            -> null
        }
    }
}

internal sealed class ValidVerificationInfoStart(
        open val transactionId: String,
        open val fromDevice: String) {
    data class SasVerificationInfoStart(
            override val transactionId: String,
            override val fromDevice: String,
            val keyAgreementProtocols: List<String>,
            val hashes: List<String>,
            val messageAuthenticationCodes: List<String>,
            val shortAuthenticationStrings: List<String>,
            val canonicalJson: String
    ) : ValidVerificationInfoStart(transactionId, fromDevice)

    data class ReciprocateVerificationInfoStart(
            override val transactionId: String,
            override val fromDevice: String,
            val sharedSecret: String
    ) : ValidVerificationInfoStart(transactionId, fromDevice)
}
