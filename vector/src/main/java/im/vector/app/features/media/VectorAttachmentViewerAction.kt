

package im.vector.app.features.media

import im.vector.app.core.platform.VectorViewModelAction
import java.io.File

sealed class VectorAttachmentViewerAction : VectorViewModelAction {
    data class DownloadMedia(val file: File) : VectorAttachmentViewerAction()
}
