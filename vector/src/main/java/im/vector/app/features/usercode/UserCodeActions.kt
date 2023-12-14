

package im.vector.app.features.usercode

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.util.MatrixItem

sealed class UserCodeActions : VectorViewModelAction {
    object DismissAction : UserCodeActions()
    data class SwitchMode(val mode: UserCodeState.Mode) : UserCodeActions()
    data class DecodedQRCode(val code: String) : UserCodeActions()
    data class StartChattingWithUser(val matrixItem: MatrixItem) : UserCodeActions()
    data class CameraPermissionNotGranted(val deniedPermanently: Boolean) : UserCodeActions()
    data class PostMatrixItem(val matrixItem: MatrixItem) : UserCodeActions()
    object ShareByText : UserCodeActions()

}
