

package im.vector.lib.multipicker

import android.content.Context
import android.content.Intent
import im.vector.lib.multipicker.entity.MultiPickerAudioType
import im.vector.lib.multipicker.utils.toMultiPickerAudioType


class AudioPicker : Picker<MultiPickerAudioType>() {

    
    override fun getSelectedFiles(context: Context, data: Intent?): List<MultiPickerAudioType> {
        return getSelectedUriList(data).mapNotNull { selectedUri ->
            selectedUri.toMultiPickerAudioType(context)
        }
    }

    override fun createIntent(): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, !single)
            type = "audio/*"
        }
    }
}
