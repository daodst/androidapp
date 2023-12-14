

package im.vector.app.features.createdirect

import im.vector.app.core.platform.VectorViewEvents

sealed class CreateDirectRoomViewEvents : VectorViewEvents {
    object InvalidCode : CreateDirectRoomViewEvents()
    object DmSelf : CreateDirectRoomViewEvents()
    class QrId(val id: String) : CreateDirectRoomViewEvents()
}
