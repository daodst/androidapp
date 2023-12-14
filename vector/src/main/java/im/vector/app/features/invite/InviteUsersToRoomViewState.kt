

package im.vector.app.features.invite

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import org.matrix.android.sdk.api.session.room.model.RoomSummary

data class InviteUsersToRoomViewState(
        val roomId: String,
        val inviteState: Async<Unit> = Uninitialized,
        val asyncRoomSummary: Async<RoomSummary> = Uninitialized,
) : MavericksState {

    constructor(args: InviteUsersToRoomArgs) : this(roomId = args.roomId)
}
