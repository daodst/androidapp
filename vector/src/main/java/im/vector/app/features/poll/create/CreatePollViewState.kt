

package im.vector.app.features.poll.create

import com.airbnb.mvrx.MavericksState
import im.vector.app.features.poll.PollMode
import org.matrix.android.sdk.api.session.room.model.message.PollType

data class CreatePollViewState(
        val roomId: String,
        val editedEventId: String?,
        val mode: PollMode,
        val question: String = "",
        val options: List<String> = List(CreatePollViewModel.MIN_OPTIONS_COUNT) { "" },
        val canCreatePoll: Boolean = false,
        val canAddMoreOptions: Boolean = true,
        val pollType: PollType = PollType.DISCLOSED_UNSTABLE
) : MavericksState {

    constructor(args: CreatePollArgs) : this(
            roomId = args.roomId,
            editedEventId = args.editedEventId,
            mode = args.mode
    )
}
