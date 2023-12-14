

package im.vector.app.features.spaces

import im.vector.app.core.platform.VectorViewModelAction

sealed class SpaceLeaveViewAction : VectorViewModelAction {
    object SetAutoLeaveAll : SpaceLeaveViewAction()
    object SetAutoLeaveNone : SpaceLeaveViewAction()
    object SetAutoLeaveSelected : SpaceLeaveViewAction()
    object LeaveSpace : SpaceLeaveViewAction()
}
