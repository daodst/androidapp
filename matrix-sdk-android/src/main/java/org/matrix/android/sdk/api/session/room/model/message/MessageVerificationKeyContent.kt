
package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoKey
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoKeyFactory

@JsonClass(generateAdapter = true)
internal data class MessageVerificationKeyContent(
        
        @Json(name = "key") override val key: String? = null,
        @Json(name = "m.relates_to") val relatesTo: RelationDefaultContent?
) : VerificationInfoKey {

    override val transactionId: String?
        get() = relatesTo?.eventId

    override fun toEventContent() = toContent()

    companion object : VerificationInfoKeyFactory {

        override fun create(tid: String, pubKey: String): VerificationInfoKey {
            return MessageVerificationKeyContent(
                    pubKey,
                    RelationDefaultContent(
                            RelationType.REFERENCE,
                            tid
                    )
            )
        }
    }
}
