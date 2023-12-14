

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent

@JsonClass(generateAdapter = true)
data class MessageNoticeContent(
        
        @Json(name = MessageContent.MSG_TYPE_JSON_KEY) override val msgType: String,

        
        @Json(name = "body") override val body: String,

        
        @Json(name = "format") override val format: String? = null,

        
        @Json(name = "formatted_body") override val formattedBody: String? = null,

        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null
) : MessageContentWithFormattedBody
