

package im.vector.app.features.invite

import im.vector.app.core.platform.VectorViewEvents

sealed class InviteUsersToRoomViewEvents : VectorViewEvents {
    object Loading : InviteUsersToRoomViewEvents()
    data class Failure(val throwable: Throwable) : InviteUsersToRoomViewEvents()
    data class Success(val successMessage: String) : InviteUsersToRoomViewEvents()
}
