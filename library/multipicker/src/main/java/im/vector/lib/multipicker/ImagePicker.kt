

package im.vector.lib.multipicker

import android.content.Context
import android.content.Intent
import im.vector.lib.multipicker.entity.MultiPickerImageType
import im.vector.lib.multipicker.utils.toMultiPickerImageType


class ImagePicker : Picker<MultiPickerImageType>() {

    
    override fun getSelectedFiles(context: Context, data: Intent?): List<MultiPickerImageType> {
        return getSelectedUriList(data).mapNotNull { selectedUri ->
            selectedUri.toMultiPickerImageType(context)
        }
    }

    override fun createIntent(): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, !single)
            type = "image/*"
        }
    }
}
