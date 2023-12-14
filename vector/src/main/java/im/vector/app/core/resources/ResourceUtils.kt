

package im.vector.app.core.resources

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import im.vector.app.core.utils.getFileExtension
import org.matrix.android.sdk.api.util.MimeTypes
import org.matrix.android.sdk.api.util.MimeTypes.normalizeMimeType
import timber.log.Timber
import java.io.InputStream

data class Resource(
        var mContentStream: InputStream? = null,
        var mMimeType: String? = null
) {
    
    fun close() {
        try {
            mMimeType = null

            mContentStream?.close()
            mContentStream = null
        } catch (e: Exception) {
            Timber.e(e, "Resource.close failed")
        }
    }

    
    fun isJpegResource(): Boolean {
        return mMimeType.normalizeMimeType() == MimeTypes.Jpeg
    }
}


fun openResource(context: Context, uri: Uri, providedMimetype: String?): Resource? {
    var mimetype = providedMimetype
    try {
        
        if (mimetype.isNullOrEmpty()) {
            mimetype = context.contentResolver.getType(uri)

            
            if (null == mimetype) {
                val extension = getFileExtension(uri.toString())
                if (extension != null) {
                    mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                }
            }
        }

        return Resource(context.contentResolver.openInputStream(uri), mimetype)
    } catch (e: Exception) {
        Timber.e(e, "Failed to open resource input stream")
    }

    return null
}
