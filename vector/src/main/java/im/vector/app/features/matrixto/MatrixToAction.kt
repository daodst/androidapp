

package im.vector.app.features.matrixto

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.util.MatrixItem

sealed class MatrixToAction : VectorViewModelAction {
    data class StartChattingWithUser(val matrixItem: MatrixItem) : MatrixToAction()
    object FailedToResolveUser : MatrixToAction()
    object FailedToStartChatting : MatrixToAction()
    data class JoinSpace(val spaceID: String, val viaServers: List<String>?) : MatrixToAction()
    data class JoinRoom(val roomIdOrAlias: String, val viaServers: List<String>?) : MatrixToAction()
    data class OpenSpace(val spaceID: String) : MatrixToAction()
    data class OpenRoom(val roomId: String) : MatrixToAction()
}
