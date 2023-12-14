

package org.matrix.android.sdk.api.session.room.model.message

import android.webkit.MimeTypeMap
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.EncryptedFileInfo
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent

@JsonClass(generateAdapter = true)
data class MessageFileContent(
        
        @Json(name = MessageContent.MSG_TYPE_JSON_KEY) override val msgType: String,

        
        @Json(name = "body") override val body: String,

        
        @Json(name = "filename") val filename: String? = null,

        
        @Json(name = "info") val info: FileInfo? = null,

        
        @Json(name = "url") override val url: String? = null,

        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null,

        
        @Json(name = "file") override val encryptedFileInfo: EncryptedFileInfo? = null
) : MessageWithAttachmentContent {

    override val mimeType: String?
        get() = info?.mimeType
                ?: MimeTypeMap.getFileExtensionFromUrl(filename ?: body)?.let { extension ->
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                }

    fun getFileName(): String {
        return filename ?: body
    }
}
