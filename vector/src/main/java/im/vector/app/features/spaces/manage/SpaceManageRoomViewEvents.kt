

package im.vector.app.features.spaces.manage

import im.vector.app.core.platform.VectorViewEvents

sealed class SpaceManageRoomViewEvents : VectorViewEvents {
    
    data class BulkActionFailure(val errorList: List<Throwable>) : SpaceManageRoomViewEvents()
}
