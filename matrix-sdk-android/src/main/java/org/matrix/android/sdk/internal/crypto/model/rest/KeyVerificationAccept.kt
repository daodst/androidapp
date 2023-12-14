
package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.SendToDeviceObject
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoAccept
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoAcceptFactory


@JsonClass(generateAdapter = true)
internal data class KeyVerificationAccept(
        
        @Json(name = "transaction_id")
        override val transactionId: String? = null,

        
        @Json(name = "key_agreement_protocol")
        override val keyAgreementProtocol: String? = null,

        
        @Json(name = "hash")
        override val hash: String? = null,

        
        @Json(name = "message_authentication_code")
        override val messageAuthenticationCode: String? = null,

        
        @Json(name = "short_authentication_string")
        override val shortAuthenticationStrings: List<String>? = null,

        
        @Json(name = "commitment")
        override var commitment: String? = null
) : SendToDeviceObject, VerificationInfoAccept {

    override fun toSendToDeviceObject() = this

    companion object : VerificationInfoAcceptFactory {
        override fun create(tid: String,
                            keyAgreementProtocol: String,
                            hash: String,
                            commitment: String,
                            messageAuthenticationCode: String,
                            shortAuthenticationStrings: List<String>): VerificationInfoAccept {
            return KeyVerificationAccept(
                    transactionId = tid,
                    keyAgreementProtocol = keyAgreementProtocol,
                    hash = hash,
                    commitment = commitment,
                    messageAuthenticationCode = messageAuthenticationCode,
                    shortAuthenticationStrings = shortAuthenticationStrings
            )
        }
    }
}
