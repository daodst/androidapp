

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent

const val GIFTS_TYPE_FLOWERS = 0
const val GIFTS_NUM_DEFAULT = 8

@JsonClass(generateAdapter = true)
data class MessageGiftsContent(

        
        @Json(name = "body") override val body: String,

        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null,

        
        @Json(name = MessageContent.MSG_TYPE_JSON_KEY) override val msgType: String = MessageType.MSGTYPE_GIFTS,

        
        @Json(name = "m.gifts.num") val giftsNum: Int = GIFTS_NUM_DEFAULT,
        
        @Json(name = "m.gifts.type") val giftsType: Int = GIFTS_TYPE_FLOWERS,

        ) : MessageContent {

}
