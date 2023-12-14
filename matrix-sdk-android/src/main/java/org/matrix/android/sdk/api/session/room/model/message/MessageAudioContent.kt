

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.EncryptedFileInfo
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent
import org.matrix.android.sdk.api.util.JsonDict

@JsonClass(generateAdapter = true)
data class MessageAudioContent(
        
        @Json(name = MessageContent.MSG_TYPE_JSON_KEY) override val msgType: String,

        
        @Json(name = "body") override val body: String,

        
        @Json(name = "info") val audioInfo: AudioInfo? = null,

        
        @Json(name = "url") override val url: String? = null,

        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null,

        
        @Json(name = "file") override val encryptedFileInfo: EncryptedFileInfo? = null,

        
        @Json(name = "org.matrix.msc1767.audio") val audioWaveformInfo: AudioWaveformInfo? = null,

        
        @Json(name = "org.matrix.msc3245.voice") val voiceMessageIndicator: JsonDict? = null,

        
        @Json(name = "izTranslate")
        val izTranslate: Boolean = false,
        
        @Json(name = "step")
        val step: Int = 0,
        @Json(name = "appid")
        val appid: String = "",
        @Json(name = "fromLan")
        val fromLan: String = "",
        @Json(name = "toLan")
        val toLan: String = "",
) : MessageWithAttachmentContent {

    override val mimeType: String?
        get() = audioInfo?.mimeType
}
