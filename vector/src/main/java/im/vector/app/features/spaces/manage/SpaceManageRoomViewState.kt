

package im.vector.app.features.spaces.manage

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.space.SpaceHierarchyData

data class SpaceManageRoomViewState(
        val spaceId: String,
        val spaceSummary: Async<RoomSummary> = Uninitialized,
        val childrenInfo: Async<SpaceHierarchyData> = Uninitialized,
        val selectedRooms: List<String> = emptyList(),
        val currentFilter: String = "",
        val actionState: Async<Unit> = Uninitialized,
        val paginationStatus: Async<Unit> = Uninitialized,
        
        val knownRoomSummaries: List<RoomSummary> = emptyList()
) : MavericksState {
    constructor(args: SpaceManageArgs) : this(
            spaceId = args.spaceId
    )
}
