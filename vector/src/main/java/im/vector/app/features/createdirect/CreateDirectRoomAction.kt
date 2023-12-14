

package im.vector.app.features.createdirect

import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.features.userdirectory.PendingSelection

sealed class CreateDirectRoomAction : VectorViewModelAction {
    data class CreateRoomAndInviteSelectedUsers(
            val selections: Set<PendingSelection>
    ) : CreateDirectRoomAction()

    data class QrScannedAction(
            val result: String
    ) : CreateDirectRoomAction()
}
