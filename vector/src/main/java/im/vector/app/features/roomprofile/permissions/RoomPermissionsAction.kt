

package im.vector.app.features.roomprofile.permissions

import im.vector.app.core.platform.VectorViewModelAction

sealed class RoomPermissionsAction : VectorViewModelAction {
    object ToggleShowAllPermissions : RoomPermissionsAction()

    data class UpdatePermission(val editablePermission: EditablePermission, val powerLevel: Int) : RoomPermissionsAction()
}
