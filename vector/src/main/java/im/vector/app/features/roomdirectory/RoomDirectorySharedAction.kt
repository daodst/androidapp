

package im.vector.app.features.roomdirectory

import im.vector.app.core.platform.VectorSharedAction


sealed class RoomDirectorySharedAction : VectorSharedAction {
    object Back : RoomDirectorySharedAction()
    object CreateRoom : RoomDirectorySharedAction()
    object Close : RoomDirectorySharedAction()
    data class CreateRoomSuccess(val createdRoomId: String) : RoomDirectorySharedAction()
    object ChangeProtocol : RoomDirectorySharedAction()
}
