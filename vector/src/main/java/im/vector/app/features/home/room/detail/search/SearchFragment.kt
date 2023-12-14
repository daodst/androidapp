

package im.vector.app.features.home.room.detail.search

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.extensions.cleanup
import im.vector.app.core.extensions.configureWith
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.extensions.trackItemsVisibilityChange
import im.vector.app.core.platform.StateView
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.FragmentSearchBinding
import im.vector.app.features.home.RoomListDisplayMode
import im.vector.app.features.home.room.detail.RoomDetailActivity
import im.vector.app.features.home.room.detail.arguments.TimelineArgs
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.session.events.model.Event
import timber.log.Timber
import javax.inject.Inject

@Parcelize
data class SearchArgs(
        val roomId: String,
        val eventId: String?,
        val rootThreadEventId: String?,
        val roomDisplayName: String?,
        val roomAvatarUrl: String?
) : Parcelable

class SearchFragment @Inject constructor(
        private val controller: SearchResultController
) : VectorBaseFragment<FragmentSearchBinding>(),
        StateView.EventCallback,
        SearchResultController.Listener {

    private val fragmentArgs: SearchArgs by args()
    private val searchViewModel: SearchViewModel by fragmentViewModel()
    

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.stateView.contentView = views.searchResultRecycler
        views.stateView.eventCallback = this

        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        controller.timeline = searchViewModel.timeline

        views.searchResultRecycler.trackItemsVisibilityChange()
        views.searchResultRecycler.configureWith(controller)
        (views.searchResultRecycler.layoutManager as? LinearLayoutManager)?.stackFromEnd = true
        controller.listener = this
    }

    override fun onDestroyView() {
        views.searchResultRecycler.cleanup()
        controller.listener = null
        super.onDestroyView()
    }

    override fun invalidate() = withState(searchViewModel) { state ->
        Timber.i("=======jues_search=============${state.searchResult.isEmpty()}========${state.asyncSearchRequest}==========================")
        if (state.searchResult.isEmpty()) {
            when (state.asyncSearchRequest) {
                is Loading -> {
                    views.stateView.state = StateView.State.Loading
                }
                is Fail    -> {
                    views.stateView.state = StateView.State.Error(errorFormatter.toHumanReadable(state.asyncSearchRequest.error))
                }
                is Success -> {
                    views.stateView.state = StateView.State.Empty(
                            title = getString(R.string.search_no_results),
                            image = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search_no_results))
                }
                else       -> Unit
            }
        } else {
            controller.setData(state)
            views.stateView.state = StateView.State.Content
        }
    }

    fun search(query: String) {
        view?.hideKeyboard()
        searchViewModel.handle(SearchAction.SearchWith(query))
    }

    override fun onRetryClicked() {
        searchViewModel.handle(SearchAction.Retry)
    }

    override fun onItemClicked(event: Event) =
            navigateToEvent(event)

    
    private fun navigateToEvent(event: Event) {
        val roomId = event.roomId ?: return

        val args = TimelineArgs(
            roomId = roomId,
            eventId = event.eventId,
            isInviteAlreadyAccepted = false,
            displayMode = RoomListDisplayMode.FILTERED
        )
        val intent = RoomDetailActivity.newIntent(requireContext(), args)
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()

    }

    override fun loadMore() {
        searchViewModel.handle(SearchAction.LoadMore)
    }
}
