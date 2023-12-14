

package im.vector.app.features.spaces.invite

import im.vector.app.core.platform.VectorViewModelAction

sealed class SpaceInviteBottomSheetAction : VectorViewModelAction {
    object DoJoin : SpaceInviteBottomSheetAction()
    object DoReject : SpaceInviteBottomSheetAction()
}
