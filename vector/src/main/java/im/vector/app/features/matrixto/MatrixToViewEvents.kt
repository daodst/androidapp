

package im.vector.app.features.matrixto

import im.vector.app.core.platform.VectorViewEvents

sealed class MatrixToViewEvents : VectorViewEvents {
    data class NavigateToRoom(val roomId: String) : MatrixToViewEvents()
    data class NavigateToSpace(val spaceId: String) : MatrixToViewEvents()
    data class ShowModalError(val error: String) : MatrixToViewEvents()
    object Dismiss : MatrixToViewEvents()
}
