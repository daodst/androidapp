

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent
import org.matrix.android.sdk.api.session.room.send.CUS_TEXT_TYPE_NORMAL

@JsonClass(generateAdapter = true)
data class MessageCusTxtContent(
        
        @Json(name = MessageContent.MSG_TYPE_JSON_KEY) override val msgType: String,

        
        @Json(name = "body") override val body: String = "",

        @Json(name = "listTitle") val listTitle: String,
        @Json(name = "nickName") val nickName: String,
        @Json(name = "title") val title: String,
        @Json(name = "content") val content: String,
        @Json(name = "forHtml") val forHtml: Boolean,

        
        @Json(name = "format") override val format: String? = null,

        @Json(name = "type") val type: Int = CUS_TEXT_TYPE_NORMAL,

        
        @Json(name = "formatted_body") override val formattedBody: String? = null,

        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null
) : MessageContentWithFormattedBody
