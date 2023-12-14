

package im.vector.app.features.spaces.explore

import im.vector.app.core.platform.VectorViewEvents

sealed class SpaceDirectoryViewEvents : VectorViewEvents {
    object Dismiss : SpaceDirectoryViewEvents()
    data class NavigateToRoom(val roomId: String) : SpaceDirectoryViewEvents()
    data class NavigateToMxToBottomSheet(val link: String) : SpaceDirectoryViewEvents()
    data class NavigateToCreateNewRoom(val currentSpaceId: String) : SpaceDirectoryViewEvents()
}
