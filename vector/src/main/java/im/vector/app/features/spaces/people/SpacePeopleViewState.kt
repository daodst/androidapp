

package im.vector.app.features.spaces.people

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.core.platform.GenericIdArgs

data class SpacePeopleViewState(
        val spaceId: String,
        val createAndInviteState: Async<String> = Uninitialized
) : MavericksState {
    constructor(args: GenericIdArgs) : this(
            spaceId = args.id
    )
}
