

package im.vector.app.features.home.room.detail

import im.vector.app.core.platform.VectorSharedAction


sealed class RoomDetailSharedAction : VectorSharedAction {
    data class SwitchToRoom(val roomId: String) : RoomDetailSharedAction()
}
