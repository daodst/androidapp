

package im.vector.app.features.roomprofile.uploads

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.features.roomprofile.RoomProfileArgs
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.uploads.UploadEvent

data class RoomUploadsViewState(
        val roomId: String = "",
        val roomSummary: Async<RoomSummary> = Uninitialized,
        
        val mediaEvents: List<UploadEvent> = emptyList(),
        val fileEvents: List<UploadEvent> = emptyList(),
        
        val asyncEventsRequest: Async<Unit> = Uninitialized,
        
        val hasMore: Boolean = true
) : MavericksState {

    constructor(args: RoomProfileArgs) : this(roomId = args.roomId)
}
