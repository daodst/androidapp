

package im.vector.lib.multipicker.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun createTemporaryMediaFile(context: Context, mediaType: MediaType): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File = context.filesDir.also { it.mkdirs() }
    val fileSuffix = when (mediaType) {
        MediaType.IMAGE -> ".jpg"
        MediaType.VIDEO -> ".mp4"
    }

    return File.createTempFile(
            "${timeStamp}_",
            fileSuffix,
            storageDir
    )
}

internal enum class MediaType {
    IMAGE, VIDEO
}
