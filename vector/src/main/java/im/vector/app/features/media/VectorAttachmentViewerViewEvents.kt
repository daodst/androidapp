

package im.vector.app.features.media

import im.vector.app.core.platform.VectorViewEvents

sealed class VectorAttachmentViewerViewEvents : VectorViewEvents {
    data class ErrorDownloadingMedia(val error: Throwable) : VectorAttachmentViewerViewEvents()
}
