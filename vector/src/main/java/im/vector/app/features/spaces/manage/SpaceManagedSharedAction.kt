

package im.vector.app.features.spaces.manage

import im.vector.app.core.platform.VectorViewModelAction

sealed class SpaceManagedSharedAction : VectorViewModelAction {
    object HandleBack : SpaceManagedSharedAction()
    object ShowLoading : SpaceManagedSharedAction()
    object HideLoading : SpaceManagedSharedAction()
    object CreateRoom : SpaceManagedSharedAction()
    object CreateSpace : SpaceManagedSharedAction()
    object ManageRooms : SpaceManagedSharedAction()
    object OpenSpaceAliasesSettings : SpaceManagedSharedAction()
    object OpenSpacePermissionSettings : SpaceManagedSharedAction()
}
