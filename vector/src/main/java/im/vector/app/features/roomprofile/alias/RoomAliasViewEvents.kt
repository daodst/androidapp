

package im.vector.app.features.roomprofile.alias

import im.vector.app.core.platform.VectorViewEvents


sealed class RoomAliasViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : RoomAliasViewEvents()
    object Success : RoomAliasViewEvents()
}
