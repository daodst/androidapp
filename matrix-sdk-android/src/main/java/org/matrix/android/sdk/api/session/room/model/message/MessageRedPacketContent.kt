

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent

@JsonClass(generateAdapter = true)
data class MessageRedPacketContent(
        
        @Json(name = MessageContent.MSG_TYPE_JSON_KEY) override val msgType: String = MessageType.MSGTYPE_RED_PACKET,

        
        @Json(name = "body") override val body: String,

        
        @Json(name = "red_packet_id") val redPacketId: String?,
        @Json(name = "transfer_num") val transferNum: String,
        @Json(name = "transfer_symbol") val transferSymbol: String,

        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null,

) : MessageContent {

}
