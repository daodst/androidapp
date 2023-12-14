

package im.vector.app.features.spaces.manage

import im.vector.app.core.platform.VectorViewEvents

sealed class SpaceManagedSharedViewEvents : VectorViewEvents {
    object Finish : SpaceManagedSharedViewEvents()
    object ShowLoading : SpaceManagedSharedViewEvents()
    object HideLoading : SpaceManagedSharedViewEvents()
    object NavigateToCreateRoom : SpaceManagedSharedViewEvents()
    object NavigateToCreateSpace : SpaceManagedSharedViewEvents()
    object NavigateToManageRooms : SpaceManagedSharedViewEvents()
    object NavigateToAliasSettings : SpaceManagedSharedViewEvents()
    object NavigateToPermissionSettings : SpaceManagedSharedViewEvents()
}
