

package im.vector.app.features.onboarding

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import im.vector.lib.multipicker.utils.readStringColumnOrNull
import javax.inject.Inject

class UriFilenameResolver @Inject constructor(private val context: Context) {

    fun getFilenameFromUri(uri: Uri): String? {
        val fallback = uri.path?.substringAfterLast('/')
        return when (uri.scheme) {
            "content" -> readResolvedDisplayName(uri) ?: fallback
            else      -> fallback
        }
    }

    private fun readResolvedDisplayName(uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.takeIf { cursor.moveToFirst() }
                    ?.readStringColumnOrNull(OpenableColumns.DISPLAY_NAME)
        }
    }
}
