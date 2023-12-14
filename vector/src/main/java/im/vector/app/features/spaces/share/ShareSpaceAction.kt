

package im.vector.app.features.spaces.share

import im.vector.app.core.platform.VectorViewModelAction

sealed class ShareSpaceAction : VectorViewModelAction {
    object InviteByMxId : ShareSpaceAction()
    object InviteByLink : ShareSpaceAction()
}
