

package im.vector.app.features.spaces.manage

import im.vector.app.core.platform.VectorViewModelAction

sealed class SpaceManageRoomViewAction : VectorViewModelAction {
    data class ToggleSelection(val roomId: String) : SpaceManageRoomViewAction()
    data class UpdateFilter(val filter: String) : SpaceManageRoomViewAction()
    object ClearSelection : SpaceManageRoomViewAction()
    data class MarkAllAsSuggested(val suggested: Boolean) : SpaceManageRoomViewAction()
    object BulkRemove : SpaceManageRoomViewAction()
    object RefreshFromServer : SpaceManageRoomViewAction()
    object LoadAdditionalItemsIfNeeded : SpaceManageRoomViewAction()
}
