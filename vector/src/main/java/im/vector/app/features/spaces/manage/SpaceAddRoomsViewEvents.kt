

package im.vector.app.features.spaces.manage

import im.vector.app.core.platform.VectorViewEvents

sealed class SpaceAddRoomsViewEvents : VectorViewEvents {
    object WarnUnsavedChanged : SpaceAddRoomsViewEvents()
    object SavedDone : SpaceAddRoomsViewEvents()
    data class SaveFailed(val reason: Throwable) : SpaceAddRoomsViewEvents()
}
