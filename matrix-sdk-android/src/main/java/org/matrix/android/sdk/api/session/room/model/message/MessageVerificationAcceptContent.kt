
package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoAccept
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoAcceptFactory

@JsonClass(generateAdapter = true)
internal data class MessageVerificationAcceptContent(
        @Json(name = "hash") override val hash: String?,
        @Json(name = "key_agreement_protocol") override val keyAgreementProtocol: String?,
        @Json(name = "message_authentication_code") override val messageAuthenticationCode: String?,
        @Json(name = "short_authentication_string") override val shortAuthenticationStrings: List<String>?,
        @Json(name = "m.relates_to") val relatesTo: RelationDefaultContent?,
        @Json(name = "commitment") override var commitment: String? = null
) : VerificationInfoAccept {

    override val transactionId: String?
        get() = relatesTo?.eventId

    override fun toEventContent() = toContent()

    companion object : VerificationInfoAcceptFactory {

        override fun create(tid: String,
                            keyAgreementProtocol: String,
                            hash: String,
                            commitment: String,
                            messageAuthenticationCode: String,
                            shortAuthenticationStrings: List<String>): VerificationInfoAccept {
            return MessageVerificationAcceptContent(
                    hash,
                    keyAgreementProtocol,
                    messageAuthenticationCode,
                    shortAuthenticationStrings,
                    RelationDefaultContent(
                            RelationType.REFERENCE,
                            tid
                    ),
                    commitment
            )
        }
    }
}
