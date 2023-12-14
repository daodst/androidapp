

package im.vector.app.features.usercode

import im.vector.app.core.platform.VectorViewEvents
import org.matrix.android.sdk.api.util.MatrixItem

sealed class UserCodeShareViewEvents : VectorViewEvents {
    object Dismiss : UserCodeShareViewEvents()
    object ShowWaitingScreen : UserCodeShareViewEvents()
    object HideWaitingScreen : UserCodeShareViewEvents()
    data class ToastMessage(val message: String) : UserCodeShareViewEvents()
    data class NavigateToRoom(val roomId: String) : UserCodeShareViewEvents()
    data class CameraPermissionNotGranted(val deniedPermanently: Boolean) : UserCodeShareViewEvents()
    data class PostMatrixItem(val item: MatrixItem) : UserCodeShareViewEvents()
    data class SharePlainText(val text: String, val title: String, val richPlainText: String) : UserCodeShareViewEvents()
}
