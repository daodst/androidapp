package im.vector.app.features.createdirect.migrate

import android.net.Uri
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized

data class MigrateGroupViewState(
        val roomId: String,
        val name: String,
        
        val avatarUri: Uri? = null,

        val asyncCreateRoomRequest: Async<String> = Uninitialized,
) : MavericksState {
    constructor(args: MigrateGroupArgs) : this(
            roomId = args.roomId,
            name = args.groupName
    )
}
