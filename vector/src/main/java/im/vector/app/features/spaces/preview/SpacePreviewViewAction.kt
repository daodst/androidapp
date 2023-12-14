

package im.vector.app.features.spaces.preview

import im.vector.app.core.platform.VectorViewModelAction

sealed class SpacePreviewViewAction : VectorViewModelAction {
    object ViewReady : SpacePreviewViewAction()
    object AcceptInvite : SpacePreviewViewAction()
    object DismissInvite : SpacePreviewViewAction()
}
