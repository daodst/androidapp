

package im.vector.app.features.spaces.explore

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo
import org.matrix.android.sdk.api.session.space.SpaceHierarchyData

data class SpaceDirectoryState(
        
        val spaceId: String,
        val currentFilter: String = "",
        val apiResults: Map<String, Async<SpaceHierarchyData>> = emptyMap(),
        val currentRootSummary: RoomSummary? = null,
        val childList: List<SpaceChildInfo> = emptyList(),
        val hierarchyStack: List<String> = emptyList(),
        
        val joinedRoomsIds: Set<String> = emptySet(),
        
        val changeMembershipStates: Map<String, ChangeMembershipState> = emptyMap(),
        val canAddRooms: Boolean = false,
        
        val knownRoomSummaries: List<RoomSummary> = emptyList(),
        val paginationStatus: Map<String, Async<Unit>> = emptyMap()
) : MavericksState {
    constructor(args: SpaceDirectoryArgs) : this(
            spaceId = args.spaceId
    )
}
