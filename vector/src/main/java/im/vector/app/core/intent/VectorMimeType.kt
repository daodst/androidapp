

package im.vector.app.core.intent

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import im.vector.app.core.utils.getFileExtension
import timber.log.Timber
import java.util.Locale


fun getMimeTypeFromUri(context: Context, uri: Uri): String? {
    var mimeType: String? = null

    try {
        mimeType = context.contentResolver.getType(uri)

        
        if (null == mimeType) {
            val extension = getFileExtension(uri.toString())
            if (extension != null) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
        }

        if (null != mimeType) {
            
            mimeType = mimeType.lowercase(Locale.ROOT)
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to open resource input stream")
    }

    return mimeType
}
