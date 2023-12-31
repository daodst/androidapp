

package im.vector.app.features.home.room.threads.list.viewmodel

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.features.home.room.threads.arguments.ThreadListArgs
import org.matrix.android.sdk.api.session.room.threads.model.ThreadSummary
import org.matrix.android.sdk.api.session.threads.ThreadTimelineEvent

data class ThreadListViewState(
        val threadSummaryList: Async<List<ThreadSummary>> = Uninitialized,
        val rootThreadEventList: Async<List<ThreadTimelineEvent>> = Uninitialized,
        val shouldFilterThreads: Boolean = false,
        val isLoading: Boolean = false,
        val roomId: String
) : MavericksState {
    constructor(args: ThreadListArgs) : this(roomId = args.roomId)
}
