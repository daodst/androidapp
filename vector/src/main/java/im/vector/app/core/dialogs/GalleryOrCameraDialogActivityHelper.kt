

package im.vector.app.core.dialogs

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import im.vector.app.R
import im.vector.app.core.dialogs.GalleryOrCameraDialogActivityHelper.Listener
import im.vector.app.core.extensions.insertBeforeLast
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.utils.PERMISSIONS_FOR_TAKING_PHOTO
import im.vector.app.core.utils.checkPermissions
import im.vector.app.core.utils.onPermissionDeniedDialog
import im.vector.app.core.utils.registerForPermissionsResult
import im.vector.app.features.media.createUCropWithDefaultSettings
import im.vector.lib.multipicker.MultiPicker
import im.vector.lib.multipicker.entity.MultiPickerImageType
import java.io.File


class GalleryOrCameraDialogActivityHelper(
        
        private val activity: AppCompatActivity,
        private val colorProvider: ColorProvider
) {
    interface Listener {
        fun onImageReady(uri: Uri?)
    }

    private val listener = activity as? Listener ?: error("colorProvider must implement GalleryOrCameraDialogHelper.Listener")

    private val takePhotoPermissionActivityResultLauncher = activity.registerForPermissionsResult { allGranted, deniedPermanently ->
        if (allGranted) {
            doOpenCamera()
        } else if (deniedPermanently) {
            activity.onPermissionDeniedDialog(R.string.denied_permission_camera)
        }
    }

    private val takePhotoActivityResultLauncher = activity.registerStartForActivityResult { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            avatarCameraUri?.let { uri ->
                MultiPicker.get(MultiPicker.CAMERA)
                        .getTakenPhoto(activity, uri)
                        ?.let { startUCrop(it) }
            }
        }
    }

    private val pickImageActivityResultLauncher = activity.registerStartForActivityResult { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            MultiPicker
                    .get(MultiPicker.IMAGE)
                    .getSelectedFiles(activity, activityResult.data)
                    .firstOrNull()
                    ?.let { startUCrop(it) }
        }
    }

    private val uCropActivityResultLauncher = activity.registerStartForActivityResult { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            activityResult.data?.let { listener.onImageReady(UCrop.getOutput(it)) }
        }
    }

    private fun startUCrop(image: MultiPickerImageType) {
        val destinationFile = File(activity.cacheDir, image.displayName.insertBeforeLast("_e_${System.currentTimeMillis()}"))
        val uri = image.contentUri
        createUCropWithDefaultSettings(colorProvider, uri, destinationFile.toUri(), activity.getString(R.string.rotate_and_crop_screen_title))
                .withAspectRatio(1f, 1f)
                .getIntent(activity)
                .let { uCropActivityResultLauncher.launch(it) }
    }

    private enum class Type {
        Camera,
        Gallery
    }

    fun show() {
        MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.attachment_type_dialog_title)
                .setItems(
                        arrayOf(
                                activity.getString(R.string.attachment_type_camera),
                                activity.getString(R.string.attachment_type_gallery)
                        )
                ) { _, which ->
                    onAvatarTypeSelected(if (which == 0) Type.Camera else Type.Gallery)
                }
                .setPositiveButton(R.string.action_cancel, null)
                .show()
    }

    private fun onAvatarTypeSelected(type: Type) {
        when (type) {
            Type.Camera  ->
                if (checkPermissions(PERMISSIONS_FOR_TAKING_PHOTO, activity, takePhotoPermissionActivityResultLauncher)) {
                    doOpenCamera()
                }
            Type.Gallery ->
                MultiPicker.get(MultiPicker.IMAGE).single().startWith(pickImageActivityResultLauncher)
        }
    }

    private var avatarCameraUri: Uri? = null
    private fun doOpenCamera() {
        avatarCameraUri = MultiPicker.get(MultiPicker.CAMERA).startWithExpectingFile(activity, takePhotoActivityResultLauncher)
    }
}
