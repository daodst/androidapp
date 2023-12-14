

package im.vector.app.features.roomdirectory.createroom

import im.vector.app.core.platform.VectorViewEvents


sealed class CreateRoomViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : CreateRoomViewEvents()
    object Quit : CreateRoomViewEvents()
}
