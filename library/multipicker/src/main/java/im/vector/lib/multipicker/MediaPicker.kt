

package im.vector.lib.multipicker

import android.content.Context
import android.content.Intent
import im.vector.lib.multipicker.entity.MultiPickerBaseMediaType
import im.vector.lib.multipicker.utils.isMimeTypeVideo
import im.vector.lib.multipicker.utils.toMultiPickerImageType
import im.vector.lib.multipicker.utils.toMultiPickerVideoType


class MediaPicker : Picker<MultiPickerBaseMediaType>() {

    
    override fun getSelectedFiles(context: Context, data: Intent?): List<MultiPickerBaseMediaType> {
        return getSelectedUriList(data).mapNotNull { selectedUri ->
            val mimeType = context.contentResolver.getType(selectedUri)

            if (mimeType.isMimeTypeVideo()) {
                selectedUri.toMultiPickerVideoType(context)
            } else {
                
                selectedUri.toMultiPickerImageType(context)
            }
        }
    }

    override fun createIntent(): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, !single)
            type = "video/*, image/*"
            val mimeTypes = arrayOf("image/*", "video/*")
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
    }
}
