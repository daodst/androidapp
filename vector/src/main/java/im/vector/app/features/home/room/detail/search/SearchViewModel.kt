

package im.vector.app.features.home.room.detail.search

import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.features.home.room.detail.timeline.factory.TimelineFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.search.EventAndSender
import org.matrix.android.sdk.api.session.search.SearchResult
import org.matrix.android.sdk.api.util.MatrixItem
import timber.log.Timber

class SearchViewModel @AssistedInject constructor(
    @Assisted private val initialState: SearchViewState,
    session: Session,
    timelineFactory: TimelineFactory,
) : VectorViewModel<SearchViewState, SearchAction, SearchViewEvents>(initialState),
    Timeline.Listener {

    private val room = session.getRoom(initialState.roomId)!!
    private val eventId = initialState.eventId

    private var currentTask: Job? = null

    private var nextBatch: String? = null

    val timeline = timelineFactory.createTimeline(
        viewModelScope,
        room,
        eventId,
        initialState.rootThreadEventId
    )

    private val searchResult = ArrayList<EventAndSender>()

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<SearchViewModel, SearchViewState> {
        override fun create(initialState: SearchViewState): SearchViewModel
    }

    companion object :
        MavericksViewModelFactory<SearchViewModel, SearchViewState> by hiltMavericksViewModelFactory()

    init {
        timeline.start(initialState.rootThreadEventId, isSearch = true)
        timeline.addListener(this)
    }

    override fun handle(action: SearchAction) {
        when (action) {
            is SearchAction.SearchWith -> handleSearchWith(action)
            is SearchAction.LoadMore -> handleLoadMore()
            is SearchAction.Retry -> handleRetry()
        }
    }

    private fun handleSearchWith(action: SearchAction.SearchWith) {
        if (action.searchTerm.isNotEmpty()) {
            setState {
                copy(
                    searchResult = emptyList(),
                    hasMoreResult = false,
                    lastBatchSize = 0,
                    searchTerm = action.searchTerm
                )
            }
            Timber.tag("jues_search").i("=================================")
            startSearching(action.searchTerm)
        }
    }

    private fun handleLoadMore() {
        
        withState {
            setState { copy(hasMoreResult = false) }
        }
    }

    private fun handleRetry() {
        
        withState {
            setState { copy(hasMoreResult = false) }
        }
    }

    private fun startSearching(searchTerm: String) = withState { state ->
        setState {
            Timber.tag("jues_search").i("================${initialState.searchTerm}=================")
            copy(asyncSearchRequest = Loading())
        }

        viewModelScope.launch(Dispatchers.IO) {
            
            val results = ArrayList<EventAndSender>()
            searchResult.map { eventAndSender ->
                val event = eventAndSender.event
                Timber.tag("jues_search").i(": ${eventAndSender.content}")
                if (eventAndSender.content?.msgType == "m.text") {
                    val body = eventAndSender.content?.body
                    if (body != null) {
                        if (body.contains("<@")) {
                            if (body.split("\n").last().contains(searchTerm)){
                                eventAndSender.body = body
                                results.add(eventAndSender)
                            }
                        } else {
                            if (body.contains(searchTerm)) {
                                eventAndSender.body = body
                                results.add(eventAndSender)
                            }
                        }
                    }
                }
            }
            setState {
                copy(
                    searchResult = results,
                    highlights = emptyList(),
                    hasMoreResult = true,
                    lastBatchSize = 0,
                    asyncSearchRequest = Success(Unit)
                )
            }
        }
    }

    @Deprecated(message = "useless")
    private fun onSearchResultSuccess(searchResult: SearchResult) = withState { state ->
        val accumulatedResult = searchResult.results.orEmpty().plus(state.searchResult)

        

        nextBatch = searchResult.nextBatch

        setState {
            copy(
                searchResult = accumulatedResult,
                highlights = searchResult.highlights.orEmpty(),
                hasMoreResult = true,
                lastBatchSize = searchResult.results.orEmpty().size,
                asyncSearchRequest = Success(Unit)
            )
        }
    }

    override fun onFindAll(snapshot: List<TimelineEvent>) {
        Timber.tag("jues_search").i("onFindAll snapshot size =: ${snapshot.size}")
        viewModelScope.launch {
            
            snapshot.map {
                
                val matrix = MatrixItem.UserItem(
                    it.senderInfo.userId,
                    displayName = it.senderInfo.displayName,
                    avatarUrl = it.senderInfo.avatarUrl
                )

                val item = EventAndSender(it.root, matrix, it.getLastMessageContent(), "")
                searchResult.add(item)
            }
        }
    }
}
