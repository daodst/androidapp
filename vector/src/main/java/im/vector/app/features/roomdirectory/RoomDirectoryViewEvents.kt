

package im.vector.app.features.roomdirectory

import im.vector.app.core.platform.VectorViewEvents


sealed class RoomDirectoryViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : RoomDirectoryViewEvents()
}
