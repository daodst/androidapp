

package im.vector.app.features.userdirectory

import im.vector.app.core.platform.VectorSharedAction

sealed class UserListSharedAction : VectorSharedAction {
    object Close : UserListSharedAction()
    object GoBack : UserListSharedAction()
    data class OnMenuItemSelected(val itemId: Int, val selections: Set<PendingSelection>) : UserListSharedAction()
    object OpenPhoneBook : UserListSharedAction()
    object AddByQrCode : UserListSharedAction()
}
