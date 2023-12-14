

package im.vector.app.features.roomprofile.permissions

import im.vector.app.core.platform.VectorViewEvents


sealed class RoomPermissionsViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : RoomPermissionsViewEvents()
    object Success : RoomPermissionsViewEvents()
}
