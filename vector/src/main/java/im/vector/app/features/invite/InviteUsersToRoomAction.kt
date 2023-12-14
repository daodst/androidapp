

package im.vector.app.features.invite

import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.features.userdirectory.PendingSelection

sealed class InviteUsersToRoomAction : VectorViewModelAction {
    data class InviteSelectedUsers(val selections: Set<PendingSelection>) : InviteUsersToRoomAction()
}
