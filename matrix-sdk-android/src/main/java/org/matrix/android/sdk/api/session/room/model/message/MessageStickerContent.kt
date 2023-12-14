

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.EncryptedFileInfo
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent

@JsonClass(generateAdapter = true)
data class MessageStickerContent(
        
        @Transient
        override val msgType: String = MessageType.MSGTYPE_STICKER_LOCAL,

        
        @Json(name = "body") override val body: String,

        
        @Json(name = "info") override val info: ImageInfo? = null,

        
        @Json(name = "url") override val url: String? = null,

        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null,

        
        @Json(name = "file") override val encryptedFileInfo: EncryptedFileInfo? = null
) : MessageImageInfoContent {
    override val mimeType: String?
        get() = info?.mimeType
}
