

package im.vector.app.features.spaces.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.extensions.cleanup
import im.vector.app.core.extensions.configureWith
import im.vector.app.core.platform.OnBackPressed
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.resources.DrawableProvider
import im.vector.app.databinding.FragmentRecyclerviewWithSearchBinding
import im.vector.app.features.roomprofile.members.RoomMemberListAction
import im.vector.app.features.roomprofile.members.RoomMemberListViewModel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import reactivecircus.flowbinding.appcompat.queryTextChanges
import javax.inject.Inject

class SpacePeopleFragment @Inject constructor(
        private val drawableProvider: DrawableProvider,
        private val colorProvider: ColorProvider,
        private val epoxyController: SpacePeopleListController
) : VectorBaseFragment<FragmentRecyclerviewWithSearchBinding>(),
        OnBackPressed, SpacePeopleListController.InteractionListener {

    private val viewModel by fragmentViewModel(SpacePeopleViewModel::class)
    private val membersViewModel by fragmentViewModel(RoomMemberListViewModel::class)
    private lateinit var sharedActionViewModel: SpacePeopleSharedActionViewModel

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentRecyclerviewWithSearchBinding.inflate(inflater, container, false)

    override fun onBackPressed(toolbarButton: Boolean): Boolean {
        sharedActionViewModel.post(SpacePeopleSharedAction.Dismiss)
        return true
    }

    override fun invalidate() = withState(membersViewModel) { memberListState ->
        val memberCount = (memberListState.roomSummary.invoke()?.otherMemberIds?.size ?: 0) + 1

        toolbar?.subtitle = resources.getQuantityString(R.plurals.room_title_members, memberCount, memberCount)
        epoxyController.setData(memberListState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedActionViewModel = activityViewModelProvider.get(SpacePeopleSharedActionViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(views.addRoomToSpaceToolbar)
                .allowBack()
        setupRecyclerView()
        setupSearchView()

        viewModel.observeViewEvents {
            handleViewEvents(it)
        }

        viewModel.onEach {
            when (it.createAndInviteState) {
                is Loading -> sharedActionViewModel.post(SpacePeopleSharedAction.ShowModalLoading)
                Uninitialized,
                is Fail    -> sharedActionViewModel.post(SpacePeopleSharedAction.HideModalLoading)
                is Success -> {
                    
                }
            }
        }
    }

    override fun onDestroyView() {
        epoxyController.listener = null
        views.roomList.cleanup()
        super.onDestroyView()
    }

    private fun setupRecyclerView() {
        views.roomList.configureWith(epoxyController, hasFixedSize = false, disableItemAnimation = false)
        epoxyController.listener = this
    }

    private fun setupSearchView() {
        views.memberNameFilter.queryHint = getString(R.string.search_members_hint)
        views.memberNameFilter.queryTextChanges()
                .debounce(100)
                .onEach {
                    membersViewModel.handle(RoomMemberListAction.FilterMemberList(it.toString()))
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleViewEvents(events: SpacePeopleViewEvents) {
        when (events) {
            is SpacePeopleViewEvents.OpenRoom      -> {
                sharedActionViewModel.post(SpacePeopleSharedAction.NavigateToRoom(events.roomId))
            }
            is SpacePeopleViewEvents.InviteToSpace -> {
                sharedActionViewModel.post(SpacePeopleSharedAction.NavigateToInvite(events.spaceId))
            }
        }
    }

    override fun onSpaceMemberClicked(roomMemberSummary: RoomMemberSummary) {
        viewModel.handle(SpacePeopleViewAction.ChatWith(roomMemberSummary))
    }

    override fun onInviteToSpaceSelected() {
        viewModel.handle(SpacePeopleViewAction.InviteToSpace)
    }
}
