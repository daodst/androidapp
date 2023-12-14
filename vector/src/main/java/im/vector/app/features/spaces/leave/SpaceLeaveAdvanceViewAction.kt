

package im.vector.app.features.spaces.leave

import im.vector.app.core.platform.VectorViewModelAction

sealed class SpaceLeaveAdvanceViewAction : VectorViewModelAction {
    data class ToggleSelection(val roomId: String) : SpaceLeaveAdvanceViewAction()
    data class UpdateFilter(val filter: String) : SpaceLeaveAdvanceViewAction()
    object DoLeave : SpaceLeaveAdvanceViewAction()
    object ClearError : SpaceLeaveAdvanceViewAction()
}
