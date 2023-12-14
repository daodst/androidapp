

package im.vector.app.features.room

import im.vector.app.core.platform.VectorViewModelAction

sealed class RequireActiveMembershipAction : VectorViewModelAction {
    data class ChangeRoom(val roomId: String) : RequireActiveMembershipAction()
}
