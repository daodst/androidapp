

package im.vector.app.features.home.room.detail.upgrade

import im.vector.app.core.platform.VectorViewModelAction

sealed class MigrateRoomAction : VectorViewModelAction {
    data class SetAutoInvite(val autoInvite: Boolean) : MigrateRoomAction()
    data class SetUpdateKnownParentSpace(val update: Boolean) : MigrateRoomAction()
    object UpgradeRoom : MigrateRoomAction()
}
