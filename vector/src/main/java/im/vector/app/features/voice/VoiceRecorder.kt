

package im.vector.app.features.voice

import org.matrix.android.sdk.api.session.content.ContentAttachmentData
import java.io.File

interface VoiceRecorder {
    
    fun initializeRecord(attachmentData: ContentAttachmentData)

    
    fun startRecord(roomId: String)

    
    fun stopRecord()

    
    fun cancelRecord()

    fun getMaxAmplitude(): Int

    
    fun getCurrentRecord(): File?

    
    fun getVoiceMessageFile(): File?
}
