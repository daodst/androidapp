

package im.vector.app.features.voice

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import org.matrix.android.sdk.api.session.content.ContentAttachmentData
import org.matrix.android.sdk.api.util.md5
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

abstract class AbstractVoiceRecorder(
        private val context: Context,
        private val filenameExt: String
) : VoiceRecorder {
    private val outputDirectory: File by lazy {
        File(context.cacheDir, "voice_records").also {
            it.mkdirs()
        }
    }

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null

    abstract fun setOutputFormat(mediaRecorder: MediaRecorder)
    abstract fun convertFile(recordedFile: File?): File?

    private fun init() {
        createMediaRecorder().let {
            it.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setOutputFormat(it)
            it.setAudioEncodingBitRate(24000)
            it.setAudioSamplingRate(48000)
            mediaRecorder = it
        }
    }

    private fun createMediaRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    override fun initializeRecord(attachmentData: ContentAttachmentData) {
        outputFile = attachmentData.findVoiceFile(outputDirectory)
    }

    override fun startRecord(roomId: String) {
        init()
        val fileName = "${UUID.randomUUID()}.$filenameExt"
        val outputDirectoryForRoom = File(outputDirectory, roomId.md5()).apply {
            mkdirs()
        }
        outputFile = File(outputDirectoryForRoom, fileName)

        val mr = mediaRecorder ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mr.setOutputFile(outputFile)
        } else {
            mr.setOutputFile(FileOutputStream(outputFile).fd)
        }
        mr.prepare()
        mr.start()
    }

    override fun stopRecord() {
        
        mediaRecorder?.let {
            it.stop()
            it.reset()
            it.release()
        }
        mediaRecorder = null
    }

    override fun cancelRecord() {
        stopRecord()

        outputFile?.delete()
        outputFile = null
    }

    override fun getMaxAmplitude(): Int {
        return mediaRecorder?.maxAmplitude ?: 0
    }

    override fun getCurrentRecord(): File? {
        return outputFile
    }

    override fun getVoiceMessageFile(): File? {
        return convertFile(outputFile)
    }
}

private fun ContentAttachmentData.findVoiceFile(baseDirectory: File): File {
    return File(baseDirectory, queryUri.takePathAfter(baseDirectory.name))
}

private fun Uri.takePathAfter(after: String): String {
    return pathSegments.takeLastWhile { it != after }.joinToString(separator = "/") { it }
}
