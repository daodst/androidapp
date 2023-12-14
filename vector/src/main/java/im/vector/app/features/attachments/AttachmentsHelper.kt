
package im.vector.app.features.attachments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import im.vector.app.core.dialogs.PhotoOrVideoDialog
import im.vector.app.core.platform.Restorable
import im.vector.app.features.settings.VectorPreferences
import im.vector.lib.multipicker.MultiPicker
import org.matrix.android.sdk.BuildConfig
import org.matrix.android.sdk.api.session.content.ContentAttachmentData
import timber.log.Timber

private const val CAPTURE_PATH_KEY = "CAPTURE_PATH_KEY"
private const val PENDING_TYPE_KEY = "PENDING_TYPE_KEY"


class AttachmentsHelper(val context: Context, val callback: Callback) : Restorable {

    interface Callback {
        fun onContactAttachmentReady(contactAttachment: ContactAttachment) {
            if (BuildConfig.LOG_PRIVATE_DATA) {
                Timber.v("On contact attachment ready: $contactAttachment")
            }
        }

        fun onContentAttachmentsReady(attachments: List<ContentAttachmentData>)
        fun onAttachmentsProcessFailed()
    }

    
    private var captureUri: Uri? = null

    
    var pendingType: AttachmentTypeSelectorView.Type? = null

    

    override fun onSaveInstanceState(outState: Bundle) {
        captureUri?.also {
            outState.putParcelable(CAPTURE_PATH_KEY, it)
        }
        pendingType?.also {
            outState.putSerializable(PENDING_TYPE_KEY, it)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        captureUri = savedInstanceState?.getParcelable(CAPTURE_PATH_KEY) as? Uri
        pendingType = savedInstanceState?.getSerializable(PENDING_TYPE_KEY) as? AttachmentTypeSelectorView.Type
    }

    

    
    fun selectFile(activityResultLauncher: ActivityResultLauncher<Intent>) {
        MultiPicker.get(MultiPicker.FILE).startWith(activityResultLauncher)
    }

    
    fun selectGallery(activityResultLauncher: ActivityResultLauncher<Intent>) {
        MultiPicker.get(MultiPicker.MEDIA).startWith(activityResultLauncher)
    }

    
    fun selectAudio(activityResultLauncher: ActivityResultLauncher<Intent>) {
        MultiPicker.get(MultiPicker.AUDIO).startWith(activityResultLauncher)
    }

    
    fun openCamera(activity: Activity,
                   vectorPreferences: VectorPreferences,
                   cameraActivityResultLauncher: ActivityResultLauncher<Intent>,
                   cameraVideoActivityResultLauncher: ActivityResultLauncher<Intent>) {
        PhotoOrVideoDialog(activity, vectorPreferences).show(object : PhotoOrVideoDialog.PhotoOrVideoDialogListener {
            override fun takePhoto() {
                captureUri = MultiPicker.get(MultiPicker.CAMERA).startWithExpectingFile(context, cameraActivityResultLauncher)
            }

            override fun takeVideo() {
                captureUri = MultiPicker.get(MultiPicker.CAMERA_VIDEO).startWithExpectingFile(context, cameraVideoActivityResultLauncher)
            }
        })
    }

    
    fun selectContact(activityResultLauncher: ActivityResultLauncher<Intent>) {
        MultiPicker.get(MultiPicker.CONTACT).startWith(activityResultLauncher)
    }

    
    fun onFileResult(data: Intent?) {
        callback.onContentAttachmentsReady(
                MultiPicker.get(MultiPicker.FILE)
                        .getSelectedFiles(context, data)
                        .map { it.toContentAttachmentData() }
        )
    }

    fun onAudioResult(data: Intent?) {
        callback.onContentAttachmentsReady(
                MultiPicker.get(MultiPicker.AUDIO)
                        .getSelectedFiles(context, data)
                        .map { it.toContentAttachmentData() }
        )
    }

    fun onContactResult(data: Intent?) {
        MultiPicker.get(MultiPicker.CONTACT)
                .getSelectedFiles(context, data)
                .firstOrNull()
                ?.toContactAttachment()
                ?.let {
                    callback.onContactAttachmentReady(it)
                }
    }

    fun onMediaResult(data: Intent?) {
        callback.onContentAttachmentsReady(
                MultiPicker.get(MultiPicker.MEDIA)
                        .getSelectedFiles(context, data)
                        .map { it.toContentAttachmentData() }
        )
    }

    fun onCameraResult() {
        captureUri?.let { captureUri ->
            MultiPicker.get(MultiPicker.CAMERA)
                    .getTakenPhoto(context, captureUri)
                    ?.let {
                        callback.onContentAttachmentsReady(
                                listOf(it).map { it.toContentAttachmentData() }
                        )
                    }
        }
    }

    fun onCameraVideoResult() {
        captureUri?.let { captureUri ->
            MultiPicker.get(MultiPicker.CAMERA_VIDEO)
                    .getTakenVideo(context, captureUri)
                    ?.let {
                        callback.onContentAttachmentsReady(
                                listOf(it).map { it.toContentAttachmentData() }
                        )
                    }
        }
    }

    fun onVideoResult(data: Intent?) {
        callback.onContentAttachmentsReady(
                MultiPicker.get(MultiPicker.VIDEO)
                        .getSelectedFiles(context, data)
                        .map { it.toContentAttachmentData() }
        )
    }

    
    fun handleShareIntent(context: Context, intent: Intent): Boolean {
        val type = intent.resolveType(context) ?: return false
        if (type.startsWith("image")) {
            callback.onContentAttachmentsReady(
                    MultiPicker.get(MultiPicker.IMAGE).getIncomingFiles(context, intent).map {
                        it.toContentAttachmentData()
                    }
            )
        } else if (type.startsWith("video")) {
            callback.onContentAttachmentsReady(
                    MultiPicker.get(MultiPicker.VIDEO).getIncomingFiles(context, intent).map {
                        it.toContentAttachmentData()
                    }
            )
        } else if (type.startsWith("audio")) {
            callback.onContentAttachmentsReady(
                    MultiPicker.get(MultiPicker.AUDIO).getIncomingFiles(context, intent).map {
                        it.toContentAttachmentData()
                    }
            )
        } else if (type.startsWith("application") || type.startsWith("file") || type.startsWith("*")) {
            callback.onContentAttachmentsReady(
                    MultiPicker.get(MultiPicker.FILE).getIncomingFiles(context, intent).map {
                        it.toContentAttachmentData()
                    }
            )
        } else {
            return false
        }
        return true
    }
}
