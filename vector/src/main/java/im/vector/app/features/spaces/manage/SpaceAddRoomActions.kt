

package im.vector.app.features.spaces.manage

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.session.room.model.RoomSummary

sealed class SpaceAddRoomActions : VectorViewModelAction {
    data class UpdateFilter(val filter: String) : SpaceAddRoomActions()
    data class ToggleSelection(val roomSummary: RoomSummary) : SpaceAddRoomActions()
    object Save : SpaceAddRoomActions()
}
