

package im.vector.app.features.home.room.detail.search

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import org.matrix.android.sdk.api.session.search.EventAndSender

data class SearchViewState(
    
    val searchResult: List<EventAndSender> = emptyList(),
    val highlights: List<String> = emptyList(),
    val hasMoreResult: Boolean = false,
    
    val lastBatchSize: Int = 0,
    val searchTerm: String? = null,
    val roomId: String = "",
    val eventId: String?,
    val rootThreadEventId: String?,
    
    val asyncSearchRequest: Async<Unit> = Uninitialized
) : MavericksState {

    constructor(args: SearchArgs) : this(
        roomId = args.roomId,
        eventId = args.eventId,
        rootThreadEventId = args.rootThreadEventId
    )
}
