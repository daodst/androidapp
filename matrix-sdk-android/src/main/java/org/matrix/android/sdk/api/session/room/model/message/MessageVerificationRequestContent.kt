
package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoRequest

@JsonClass(generateAdapter = true)
data class MessageVerificationRequestContent(
        @Json(name = MessageContent.MSG_TYPE_JSON_KEY) override val msgType: String = MessageType.MSGTYPE_VERIFICATION_REQUEST,
        @Json(name = "body") override val body: String,
        @Json(name = "from_device") override val fromDevice: String?,
        @Json(name = "methods") override val methods: List<String>,
        @Json(name = "to") val toUserId: String,
        @Json(name = "timestamp") override val timestamp: Long?,
        @Json(name = "format") val format: String? = null,
        @Json(name = "formatted_body") val formattedBody: String? = null,
        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null,
        
        override val transactionId: String? = null
) : MessageContent, VerificationInfoRequest {

    override fun toEventContent() = toContent()
}
