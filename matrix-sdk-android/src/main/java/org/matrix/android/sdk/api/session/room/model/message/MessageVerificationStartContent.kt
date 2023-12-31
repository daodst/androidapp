
package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoStart
import org.matrix.android.sdk.internal.util.JsonCanonicalizer

@JsonClass(generateAdapter = true)
internal data class MessageVerificationStartContent(
        @Json(name = "from_device") override val fromDevice: String?,
        @Json(name = "hashes") override val hashes: List<String>?,
        @Json(name = "key_agreement_protocols") override val keyAgreementProtocols: List<String>?,
        @Json(name = "message_authentication_codes") override val messageAuthenticationCodes: List<String>?,
        @Json(name = "short_authentication_string") override val shortAuthenticationStrings: List<String>?,
        @Json(name = "method") override val method: String?,
        @Json(name = "m.relates_to") val relatesTo: RelationDefaultContent?,
        @Json(name = "secret") override val sharedSecret: String?
) : VerificationInfoStart {

    override fun toCanonicalJson(): String {
        return JsonCanonicalizer.getCanonicalJson(MessageVerificationStartContent::class.java, this)
    }

    override val transactionId: String?
        get() = relatesTo?.eventId

    override fun toEventContent() = toContent()
}
