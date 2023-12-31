

package im.vector.app.features.voice

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File

@RequiresApi(Build.VERSION_CODES.Q)
class VoiceRecorderQ(context: Context) : AbstractVoiceRecorder(context, "ogg") {
    override fun setOutputFormat(mediaRecorder: MediaRecorder) {
        
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.OGG)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.OPUS)
    }

    override fun convertFile(recordedFile: File?): File? {
        
        return recordedFile
    }
}
