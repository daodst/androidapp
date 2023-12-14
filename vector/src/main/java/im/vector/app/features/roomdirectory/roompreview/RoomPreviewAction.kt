

package im.vector.app.features.roomdirectory.roompreview

import im.vector.app.core.platform.VectorViewModelAction

sealed class RoomPreviewAction : VectorViewModelAction {
    object Join : RoomPreviewAction()
    object JoinThirdParty : RoomPreviewAction()
}
