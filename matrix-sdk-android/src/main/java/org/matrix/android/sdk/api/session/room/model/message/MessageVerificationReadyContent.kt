
package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent
import org.matrix.android.sdk.internal.crypto.verification.MessageVerificationReadyFactory
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoReady

@JsonClass(generateAdapter = true)
internal data class MessageVerificationReadyContent(
        @Json(name = "from_device") override val fromDevice: String? = null,
        @Json(name = "methods") override val methods: List<String>? = null,
        @Json(name = "m.relates_to") val relatesTo: RelationDefaultContent?
) : VerificationInfoReady {

    override val transactionId: String?
        get() = relatesTo?.eventId

    override fun toEventContent() = toContent()

    companion object : MessageVerificationReadyFactory {
        override fun create(tid: String, methods: List<String>, fromDevice: String): VerificationInfoReady {
            return MessageVerificationReadyContent(
                    fromDevice = fromDevice,
                    methods = methods,
                    relatesTo = RelationDefaultContent(
                            RelationType.REFERENCE,
                            tid
                    )
            )
        }
    }
}
