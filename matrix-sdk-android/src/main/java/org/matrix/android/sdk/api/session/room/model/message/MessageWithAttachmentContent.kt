

package org.matrix.android.sdk.api.session.room.model.message

import org.matrix.android.sdk.api.session.crypto.model.EncryptedFileInfo


interface MessageWithAttachmentContent : MessageContent {
    
    val url: String?

    
    val encryptedFileInfo: EncryptedFileInfo?

    val mimeType: String?
}


fun MessageWithAttachmentContent.getFileUrl() = encryptedFileInfo?.url ?: url

fun MessageWithAttachmentContent.getFileName() = (this as? MessageFileContent)?.getFileName() ?: body
