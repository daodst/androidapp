

package im.vector.app.features.voice

import android.content.Context
import android.os.Build
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class VoicePlayerHelper @Inject constructor(
        context: Context
) {
    private val outputDirectory: File by lazy {
        File(context.cacheDir, "voice_records").also {
            it.mkdirs()
        }
    }

    
    fun convertFile(file: File): File? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            
            file
        } else {
            
            val targetFile = File(outputDirectory, "Voice.mp4")
            if (targetFile.exists()) {
                targetFile.delete()
            }
            val start = System.currentTimeMillis()
            val session = FFmpegKit.execute("-i \"${file.path}\" -c:a aac \"${targetFile.path}\"")
            val duration = System.currentTimeMillis() - start
            Timber.d("Convert to mp4 in $duration ms. Size in bytes from ${file.length()} to ${targetFile.length()}")
            return when {
                ReturnCode.isSuccess(session.returnCode) -> {
                    
                    targetFile
                }
                ReturnCode.isCancel(session.returnCode)  -> {
                    
                    null
                }
                else                                     -> {
                    
                    Timber.e("Command failed with state ${session.state} and rc ${session.returnCode}.${session.failStackTrace}")
                    
                    null
                }
            }
        }
    }
}
