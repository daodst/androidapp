

package im.vector.app.features.roomdirectory

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoom

data class PublicRoomsViewState(
        
        val currentFilter: String = "",
        
        val publicRooms: List<PublicRoom> = emptyList(),
        
        val asyncPublicRoomsRequest: Async<Unit> = Uninitialized,
        
        val hasMore: Boolean = false,
        
        val joinedRoomsIds: Set<String> = emptySet(),
        
        val changeMembershipStates: Map<String, ChangeMembershipState> = emptyMap(),
        val roomDirectoryData: RoomDirectoryData = RoomDirectoryData()
) : MavericksState
