

package im.vector.lib.multipicker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import im.vector.lib.multipicker.entity.MultiPickerImageType
import im.vector.lib.multipicker.utils.MediaType
import im.vector.lib.multipicker.utils.createTemporaryMediaFile
import im.vector.lib.multipicker.utils.toMultiPickerImageType


class CameraPicker {

    
    fun startWithExpectingFile(context: Context, activityResultLauncher: ActivityResultLauncher<Intent>): Uri {
        val photoUri = createPhotoUri(context)
        val intent = createIntent().apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        activityResultLauncher.launch(intent)
        return photoUri
    }

    
    fun getTakenPhoto(context: Context, photoUri: Uri): MultiPickerImageType? {
        return photoUri.toMultiPickerImageType(context)
    }

    private fun createIntent(): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }

    companion object {
        fun createPhotoUri(context: Context): Uri {
            val file = createTemporaryMediaFile(context, MediaType.IMAGE)
            val authority = context.packageName + ".multipicker.fileprovider"
            return FileProvider.getUriForFile(context, authority, file)
        }
    }
}
