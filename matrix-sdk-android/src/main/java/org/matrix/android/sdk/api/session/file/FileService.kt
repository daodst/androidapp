

package org.matrix.android.sdk.api.session.file

import android.net.Uri
import org.matrix.android.sdk.api.session.crypto.attachments.ElementToDecrypt
import org.matrix.android.sdk.api.session.crypto.attachments.toElementToDecrypt
import org.matrix.android.sdk.api.session.room.model.message.MessageWithAttachmentContent
import org.matrix.android.sdk.api.session.room.model.message.getFileName
import org.matrix.android.sdk.api.session.room.model.message.getFileUrl
import java.io.File


interface FileService {

    sealed class FileState {
        
        data class InCache(val decryptedFileInCache: Boolean) : FileState()
        object Downloading : FileState()
        object Unknown : FileState()
    }

    
    suspend fun downloadFile(fileName: String,
                             mimeType: String?,
                             url: String?,
                             elementToDecrypt: ElementToDecrypt?): File

    suspend fun downloadCusFile(fileName: String,
                                mimeType: String?,
                                text: String,
                                lan: String,
                                url: String?): File

    suspend fun downloadFile(messageContent: MessageWithAttachmentContent): File =
            downloadFile(
                    fileName = messageContent.getFileName(),
                    mimeType = messageContent.mimeType,
                    url = messageContent.getFileUrl(),
                    elementToDecrypt = messageContent.encryptedFileInfo?.toElementToDecrypt()
            )

    fun isFileInCache(mxcUrl: String?,
                      fileName: String,
                      mimeType: String?,
                      elementToDecrypt: ElementToDecrypt?
    ): Boolean

    fun isFileInCache(messageContent: MessageWithAttachmentContent) =
            isFileInCache(
                    mxcUrl = messageContent.getFileUrl(),
                    fileName = messageContent.getFileName(),
                    mimeType = messageContent.mimeType,
                    elementToDecrypt = messageContent.encryptedFileInfo?.toElementToDecrypt()
            )

    
    fun getTemporarySharableURI(mxcUrl: String?,
                                fileName: String,
                                mimeType: String?,
                                elementToDecrypt: ElementToDecrypt?): Uri?

    fun getTemporarySharableURI(messageContent: MessageWithAttachmentContent): Uri? =
            getTemporarySharableURI(
                    mxcUrl = messageContent.getFileUrl(),
                    fileName = messageContent.getFileName(),
                    mimeType = messageContent.mimeType,
                    elementToDecrypt = messageContent.encryptedFileInfo?.toElementToDecrypt()
            )

    
    fun fileState(mxcUrl: String?,
                  fileName: String,
                  mimeType: String?,
                  elementToDecrypt: ElementToDecrypt?): FileState

    fun fileState(messageContent: MessageWithAttachmentContent): FileState =
            fileState(
                    mxcUrl = messageContent.getFileUrl(),
                    fileName = messageContent.getFileName(),
                    mimeType = messageContent.mimeType,
                    elementToDecrypt = messageContent.encryptedFileInfo?.toElementToDecrypt()
            )

    
    fun clearCache()

    
    fun clearDecryptedCache()

    
    fun getCacheSize(): Long
}
