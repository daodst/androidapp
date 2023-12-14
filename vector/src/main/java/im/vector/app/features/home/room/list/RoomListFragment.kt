

package im.vector.app.features.home.room.list

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.OnModelBuildFinishedListener
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import common.app.utils.AppWidgetUtils
import im.vector.app.R
import im.vector.app.core.epoxy.LayoutManagerStateRestorer
import im.vector.app.core.extensions.cleanup
import im.vector.app.core.platform.OnBackPressed
import im.vector.app.core.platform.StateView
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.core.resources.UserPreferencesProvider
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.databinding.FragmentRoomListBinding
import im.vector.app.features.analytics.plan.MobileScreen
import im.vector.app.features.home.RoomListDisplayMode
import im.vector.app.features.home.room.filtered.FilteredRoomFooterItem
import im.vector.app.features.home.room.list.actions.RoomListQuickActionsBottomSheet
import im.vector.app.features.home.room.list.actions.RoomListQuickActionsSharedAction
import im.vector.app.features.home.room.list.actions.RoomListQuickActionsSharedActionViewModel
import im.vector.app.features.home.room.list.widget.NotifsFabMenuView
import im.vector.app.features.notifications.NotificationDrawerManager
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.extensions.orTrue
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.izServerNoticeId
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo
import org.matrix.android.sdk.api.session.room.model.tag.RoomTag
import org.matrix.android.sdk.api.session.room.notification.RoomNotificationState
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

@Parcelize
data class RoomListParams(
        val displayMode: RoomListDisplayMode
) : Parcelable

class RoomListFragment @Inject constructor(
        private val pagedControllerFactory: RoomSummaryPagedControllerFactory,
        private val notificationDrawerManager: NotificationDrawerManager,
        private val footerController: RoomListFooterController,
        private val userPreferencesProvider: UserPreferencesProvider,
        private val dimensionConverter: DimensionConverter,
        private val session: Session,
) : VectorBaseFragmentHost<FragmentRoomListBinding>(),
        RoomListListener,
        OnBackPressed,
        FilteredRoomFooterItem.Listener,
        NotifsFabMenuView.Listener {

    private var modelBuildListener: OnModelBuildFinishedListener? = null
    private lateinit var sharedActionViewModel: RoomListQuickActionsSharedActionViewModel
    private val roomListParams: RoomListParams by args()
    private val roomListViewModel: RoomListViewModel by fragmentViewModel()
    private lateinit var stateRestorer: LayoutManagerStateRestorer

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): FragmentRoomListBinding {
        return FragmentRoomListBinding.inflate(inflater, container, false)
    }

    data class SectionKey(
            val name: String,
            val isExpanded: Boolean,
            val notifyOfLocalEcho: Boolean
    )

    data class SectionAdapterInfo(
            var section: SectionKey,
            val sectionHeaderAdapter: SectionHeaderAdapter,
            val contentEpoxyController: EpoxyController
    )

    private val adapterInfosList = mutableListOf<SectionAdapterInfo>()
    private var concatAdapter: ConcatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsScreenName = when (roomListParams.displayMode) {
            RoomListDisplayMode.PEOPLE -> MobileScreen.ScreenName.People
            RoomListDisplayMode.ROOMS  -> MobileScreen.ScreenName.Rooms
            RoomListDisplayMode.GROUP  -> MobileScreen.ScreenName.Group
            else                       -> null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.stateView.contentView = views.roomListViewParent
        views.stateView.state = StateView.State.Loading
        views.stateView.groupCallback = object : StateView.CreateGroupEventCallback {
            override fun onClicked() {
                val intent = Intent()
                intent.setClassName(vectorBaseActivity, "com.app.view.groupinformation.introduction.IntroductionPageActivity".trim())
                startActivity(intent)
            }
        }



        setupCreateRoomButton()
        setupRecyclerView()
        sharedActionViewModel =
                activityViewModelProvider.get(RoomListQuickActionsSharedActionViewModel::class.java)
        roomListViewModel.observeViewEvents {
            when (it) {
                is RoomListViewEvents.Loading                   -> showLoading(it.message)
                is RoomListViewEvents.Failure                   -> showFailure(it.throwable)
                is RoomListViewEvents.SelectRoom                -> handleSelectRoom(it, it.isInviteAlreadyAccepted)
                is RoomListViewEvents.Done                      -> Unit
                is RoomListViewEvents.NavigateToMxToBottomSheet -> handleShowMxToLink(it.link)
                is RoomListViewEvents.SendWidgetBroadCast       -> handleWidgetBroadCast(it.data)
            }
        }

        views.createChatFabMenu.listener = this

        sharedActionViewModel
                .stream()
                .onEach { handleQuickActions(it) }
                .launchIn(viewLifecycleOwner.lifecycleScope)

        roomListViewModel.onEach(RoomListViewState::roomMembershipChanges) { ms ->
            
            adapterInfosList.filter { it.section.notifyOfLocalEcho }
                    .onEach {
                        (it.contentEpoxyController as? RoomSummaryPagedController)?.roomChangeMembershipStates =
                                ms
                    }
        }
    }

    private fun refreshCollapseStates() {
        val sectionsCount =
                adapterInfosList.count { !it.sectionHeaderAdapter.roomsSectionData.isHidden }
        roomListViewModel.sections.forEachIndexed { index, roomsSection ->
            val actualBlock = adapterInfosList[index]
            val isRoomSectionCollapsable = sectionsCount > 1
            val isRoomSectionExpanded = roomsSection.isExpanded.value.orTrue()
            if (actualBlock.section.isExpanded && !isRoomSectionExpanded) {
                
                actualBlock.contentEpoxyController.setCollapsed(true)
            } else if (!actualBlock.section.isExpanded && isRoomSectionExpanded) {
                
                actualBlock.contentEpoxyController.setCollapsed(false)
            }
            actualBlock.section = actualBlock.section.copy(isExpanded = isRoomSectionExpanded)
            actualBlock.sectionHeaderAdapter.updateSection {
                it.copy(
                        isExpanded = isRoomSectionExpanded,
                        isCollapsable = isRoomSectionCollapsable
                )
            }

            if (!isRoomSectionExpanded && !isRoomSectionCollapsable) {
                
                roomListViewModel.handle(RoomListAction.ToggleSection(roomsSection))
            }
        }
    }

    override fun showFailure(throwable: Throwable) {
        showErrorInSnackbar(throwable)
    }

    private fun handleShowMxToLink(link: String) {
        navigator.openMatrixToBottomSheet(requireContext(), link)
    }

    
    private fun handleWidgetBroadCast(data: String) {
        
        val broadCastIntent = Intent(AppWidgetUtils.ChatWidgetDataFilter)
        val bundle = Bundle()
        bundle.putString("data", data)
        bundle.putInt("tag", Random(10).nextInt())
        broadCastIntent.putExtra("bundle", bundle)
        
        context?.sendBroadcast(broadCastIntent)

    }

    override fun onDestroyView() {
        adapterInfosList.onEach {
            it.contentEpoxyController.removeModelBuildListener(
                    modelBuildListener
            )
        }
        adapterInfosList.clear()
        modelBuildListener = null
        views.roomListView.cleanup()
        footerController.listener = null
        
        stateRestorer.clear()
        views.createChatFabMenu.listener = null
        concatAdapter = null
        super.onDestroyView()
    }

    private fun handleSelectRoom(
            event: RoomListViewEvents.SelectRoom,
            isInviteAlreadyAccepted: Boolean
    ) {

        navigator.openRoom(
                context = requireActivity(),
                roomId = event.roomSummary.roomId,
                isInviteAlreadyAccepted = isInviteAlreadyAccepted,
                displayMode = roomListParams.displayMode
        )
    }

    private fun setupCreateRoomButton() {
        when (roomListParams.displayMode) {
            RoomListDisplayMode.NOTIFICATIONS -> views.createChatFabMenu.isVisible = true
            RoomListDisplayMode.PEOPLE        -> {
                views.createChatRoomButton.isVisible = false
                (views.createChatRoomButton.layoutParams as MarginLayoutParams).marginEnd =
                        dimensionConverter.dpToPx(80)
                views.createGroupRoomButton.isVisible = false
            }
            RoomListDisplayMode.ROOMS         -> {
                views.createChatRoomButton.isVisible = false
                views.createGroupRoomButton.isVisible = false
            }
            else                              -> Unit 
        }

        views.createChatRoomButton.debouncedClicks {
            fabCreateDirectChat()
        }
        views.createGroupRoomButton.debouncedClicks {
            fabOpenRoomDirectory()
        }

        
        views.roomListView.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        views.createChatFabMenu.removeCallbacks(showFabRunnable)

                        when (newState) {
                            RecyclerView.SCROLL_STATE_IDLE     -> {
                                views.createChatFabMenu.postDelayed(showFabRunnable, 250)
                            }
                            RecyclerView.SCROLL_STATE_DRAGGING,
                            RecyclerView.SCROLL_STATE_SETTLING -> {
                                when (roomListParams.displayMode) {
                                    RoomListDisplayMode.NOTIFICATIONS -> views.createChatFabMenu.hide()
                                    RoomListDisplayMode.PEOPLE        -> {
                                        views.createChatRoomButton.hide()
                                        views.createGroupRoomButton.hide()
                                    }
                                    RoomListDisplayMode.ROOMS         -> {
                                        views.createChatRoomButton.hide()
                                        views.createGroupRoomButton.hide()
                                    }
                                    else                              -> Unit
                                }
                            }
                        }
                    }
                })
    }

    fun filterRoomsWith(filter: String) {
        
        views.roomListView.scrollToPosition(0)

        roomListViewModel.handle(RoomListAction.FilterWith(filter))
    }

    
    override fun createRoom(initialName: String) {
        navigator.openCreateRoom(requireActivity(), initialName)
    }

    override fun createDirectChat() {
        navigator.openCreateDirectRoom(requireActivity())
    }

    override fun openRoomDirectory(initialFilter: String) {
        navigator.openRoomDirectory(requireActivity(), initialFilter)
    }

    
    override fun fabCreateDirectChat() {
        navigator.openCreateDirectRoom(requireActivity())
    }

    override fun fabOpenRoomDirectory() {
        navigator.openRoomDirectory(requireActivity(), "")
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        stateRestorer = LayoutManagerStateRestorer(layoutManager).register()
        views.roomListView.layoutManager = layoutManager
        views.roomListView.itemAnimator = RoomListAnimator()
        layoutManager.recycleChildrenOnDetach = true

        modelBuildListener = OnModelBuildFinishedListener { it.dispatchTo(stateRestorer) }

        val concatAdapter = ConcatAdapter()
        concatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                var isShow = false
                concatAdapter.adapters.filterIsInstance<SectionHeaderAdapter>().forEachIndexed { _, adapter ->

                    val show = if (adapter.roomsSectionData.isHidden || isShow) {
                        false
                    } else {
                        isShow = true
                        true
                    }
                    adapter.updateSection {
                        it.copy(
                                show = show
                        )
                    }
                }
            }
        })
        roomListViewModel.sections.forEachIndexed { index, section ->
            Timber.d("section$index=$section")
            val sectionAdapter =
                    SectionHeaderAdapter(SectionHeaderAdapter.RoomsSectionData(section.sectionName )) {
                        if (it.getTag() == "chatNote") {
                            val intent = Intent()
                            intent.setClassName(vectorBaseActivity, "com.app.note.NoteListActivity")
                            startActivity(intent)
                        } else if (adapterInfosList[index].sectionHeaderAdapter.roomsSectionData.isCollapsable) {
                            roomListViewModel.handle(RoomListAction.ToggleSection(section))
                        }
                    }
            val contentAdapter =
                    when {
                        section.livePages != null     -> {
                            pagedControllerFactory.createRoomSummaryPagedController()
                                    .also { controller ->
                                        section.livePages.observe(viewLifecycleOwner) { pl ->
                                            controller.submitList(pl)
                                            val isHidden = pl.isEmpty()
                                            sectionAdapter.updateSection {
                                                it.copy(
                                                        isHidden = isHidden,
                                                        isLoading = false,
                                                )
                                            }
                                            refreshCollapseStates()
                                            checkEmptyState()
                                            
                                            
                                            roomListViewModel.handle(RoomListAction.SendWidgetBroadCast(pl, index))
                                        }
                                        observeItemCount(section, sectionAdapter)
                                        observerNotificationCount(section, sectionAdapter)
                                        section.isExpanded.observe(viewLifecycleOwner) { _ ->
                                            refreshCollapseStates()
                                        }
                                        controller.listener = this
                                    }
                        }
                        section.liveSuggested != null -> {
                            pagedControllerFactory.createSuggestedRoomListController()
                                    .also { controller ->
                                        section.liveSuggested.observe(viewLifecycleOwner) { info ->
                                            controller.setData(info)
                                            sectionAdapter.updateSection {
                                                it.copy(
                                                        isHidden = info.rooms.isEmpty(),
                                                        isLoading = false
                                                )
                                            }
                                            refreshCollapseStates()
                                            checkEmptyState()
                                        }
                                        observeItemCount(section, sectionAdapter)
                                        section.isExpanded.observe(viewLifecycleOwner) { _ ->
                                            refreshCollapseStates()
                                        }
                                        controller.listener = this
                                    }
                        }
                        else                          -> {
                            pagedControllerFactory.createRoomSummaryListController()
                                    .also { controller ->
                                        section.liveList?.observe(viewLifecycleOwner) { list ->
                                            controller.setData(list)
                                            sectionAdapter.updateSection {
                                                it.copy(
                                                        isHidden = list.isEmpty(),
                                                        isLoading = false,
                                                )
                                            }
                                            refreshCollapseStates()
                                            checkEmptyState()
                                        }
                                        observeItemCount(section, sectionAdapter)
                                        observerNotificationCount(section, sectionAdapter)
                                        section.isExpanded.observe(viewLifecycleOwner) { _ ->
                                            refreshCollapseStates()
                                        }
                                        controller.listener = this
                                    }
                        }
                    }
            adapterInfosList.add(
                    SectionAdapterInfo(
                            SectionKey(
                                    name = section.sectionName,
                                    isExpanded = section.isExpanded.value.orTrue(),
                                    notifyOfLocalEcho = section.notifyOfLocalEcho
                            ),
                            sectionAdapter,
                            contentAdapter
                    )
            )
            concatAdapter.addAdapter(sectionAdapter)
            concatAdapter.addAdapter(contentAdapter.adapter)
        }

        
        footerController.listener = this
        concatAdapter.addAdapter(footerController.adapter)

        this.concatAdapter = concatAdapter
        views.roomListView.adapter = concatAdapter
    }

    private val showFabRunnable = Runnable {
        if (isAdded) {
            when (roomListParams.displayMode) {
                RoomListDisplayMode.NOTIFICATIONS -> views.createChatFabMenu.show()
                RoomListDisplayMode.PEOPLE        -> {
                    views.createChatRoomButton.hide()
                    views.createGroupRoomButton.hide()
                }
                RoomListDisplayMode.ROOMS         -> {
                    views.createChatRoomButton.hide()
                    views.createGroupRoomButton.hide()
                }
                else                              -> Unit
            }
        }
    }

    private fun observeItemCount(section: RoomsSection, sectionAdapter: SectionHeaderAdapter) {
        lifecycleScope.launch {
            section.itemCount
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .filter { it > 0 }
                    .collect { count ->
                        sectionAdapter.updateSection {
                            it.copy(itemCount = count)
                        }
                    }
        }
    }

    
    private fun observerNotificationCount(section: RoomsSection, sectionAdapter: SectionHeaderAdapter) {
        section.notificationCount.observe(viewLifecycleOwner) { counts ->
            sectionAdapter.updateSection {
                it.copy(
                        notificationCount = counts.totalCount,
                        isHighlighted = counts.isHighlight
                )
            }
        }
    }

    private fun handleQuickActions(quickAction: RoomListQuickActionsSharedAction) {
        when (quickAction) {
            is RoomListQuickActionsSharedAction.NotificationsAllNoisy     -> {
                roomListViewModel.handle(
                        RoomListAction.ChangeRoomNotificationState(
                                quickAction.roomId,
                                RoomNotificationState.ALL_MESSAGES_NOISY
                        )
                )
            }
            is RoomListQuickActionsSharedAction.NotificationsAll          -> {
                roomListViewModel.handle(
                        RoomListAction.ChangeRoomNotificationState(
                                quickAction.roomId,
                                RoomNotificationState.ALL_MESSAGES
                        )
                )
            }
            is RoomListQuickActionsSharedAction.NotificationsMentionsOnly -> {
                roomListViewModel.handle(
                        RoomListAction.ChangeRoomNotificationState(
                                quickAction.roomId,
                                RoomNotificationState.MENTIONS_ONLY
                        )
                )
            }
            is RoomListQuickActionsSharedAction.NotificationsMute         -> {
                roomListViewModel.handle(
                        RoomListAction.ChangeRoomNotificationState(
                                quickAction.roomId,
                                RoomNotificationState.MUTE
                        )
                )
            }
            is RoomListQuickActionsSharedAction.Settings                  -> {
                navigator.openRoomProfile(requireActivity(), quickAction.roomId)
            }
            is RoomListQuickActionsSharedAction.Favorite                  -> {
                roomListViewModel.handle(
                        RoomListAction.ToggleTag(
                                quickAction.roomId,
                                RoomTag.ROOM_TAG_FAVOURITE
                        )
                )
            }
            is RoomListQuickActionsSharedAction.LowPriority               -> {
                roomListViewModel.handle(
                        RoomListAction.ToggleTag(
                                quickAction.roomId,
                                RoomTag.ROOM_TAG_LOW_PRIORITY
                        )
                )
            }
            is RoomListQuickActionsSharedAction.Leave                     -> {
                promptLeaveRoom(quickAction.roomId)
            }
        }
    }

    private fun promptLeaveRoom(roomId: String) {
        val isPublicRoom = roomListViewModel.isPublicRoom(roomId)
        val message = buildString {
            append(getString(R.string.room_participants_leave_prompt_msg))
            if (!isPublicRoom) {
                append("\n\n")
                append(getString(R.string.room_participants_leave_private_warning))
            }
        }
        MaterialAlertDialogBuilder(
                requireContext(),
                if (isPublicRoom) 0 else R.style.ThemeOverlay_Vector_MaterialAlertDialog_Destructive
        )
                .setTitle(R.string.room_participants_leave_prompt_title)
                .setMessage(message)
                .setPositiveButton(R.string.action_leave) { _, _ ->
                    roomListViewModel.handle(RoomListAction.LeaveRoom(roomId))
                }
                .setNegativeButton(R.string.action_cancel, null)
                .show()
    }

    override fun invalidate() = withState(roomListViewModel) { state ->
        footerController.setData(state)
    }

    private fun checkEmptyState() {
        val shouldShowEmpty =
                adapterInfosList.all { it.sectionHeaderAdapter.roomsSectionData.isHidden } &&
                        !adapterInfosList.any { it.sectionHeaderAdapter.roomsSectionData.isLoading }
        if (shouldShowEmpty) {
            val emptyState = when (roomListParams.displayMode) {
                RoomListDisplayMode.NOTIFICATIONS -> {
                    StateView.State.Empty(
                            title = getString(R.string.room_list_catchup_empty_title),
                            image = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_noun_party_popper
                            ),
                            message = getString(R.string.room_list_catchup_empty_body)
                    )
                }
                RoomListDisplayMode.PEOPLE        ->
                    StateView.State.Empty(
                            title = getString(R.string.room_list_people_empty_title),
                            image = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.empty_state_dm
                            ),
                            isBigImage = true,
                            message = getString(R.string.room_list_people_empty_body)
                    )
                RoomListDisplayMode.GROUP         -> {
                    StateView.State.CusEmpty
                }
                RoomListDisplayMode.ROOMS         ->
                    StateView.State.Empty(
                            title = getString(R.string.room_list_rooms_empty_title),
                            image = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.empty_state_room
                            ),
                            isBigImage = true,
                            message = getString(R.string.room_list_rooms_empty_body)
                    )
                else                              ->
                    
                    StateView.State.Content
            }
            views.stateView.state = emptyState
        } else {
            
            if (adapterInfosList.any { !it.sectionHeaderAdapter.roomsSectionData.isHidden }) {
                views.stateView.state = StateView.State.Content
            } else {
                views.stateView.state = StateView.State.Loading
            }
        }
    }

    override fun onBackPressed(toolbarButton: Boolean): Boolean {
        if (views.createChatFabMenu.onBackPressed()) {
            return true
        }
        return false
    }

    

    override fun onRoomClicked(room: RoomSummary) {
        roomListViewModel.handle(RoomListAction.SelectRoom(room))
    }

    override fun onRoomLongClicked(room: RoomSummary): Boolean {

        val izServerNotice = izServerNoticeId(room.roomId, session.myUserId)
        if (izServerNotice) {
            return true
        }
        userPreferencesProvider.neverShowLongClickOnRoomHelpAgain()
        withState(roomListViewModel) {
            
            footerController.setData(it)
        }
        RoomListQuickActionsBottomSheet
                .newInstance(room.roomId)
                .show(childFragmentManager, "ROOM_LIST_QUICK_ACTIONS")
        return true
    }

    override fun onAcceptRoomInvitation(room: RoomSummary) {
        notificationDrawerManager.updateEvents { it.clearMemberShipNotificationForRoom(room.roomId) }
        roomListViewModel.handle(RoomListAction.AcceptInvitation(room))
    }

    override fun onJoinSuggestedRoom(room: SpaceChildInfo) {
        roomListViewModel.handle(
                RoomListAction.JoinSuggestedRoom(
                        room.childRoomId,
                        room.viaServers
                )
        )
    }

    override fun onSuggestedRoomClicked(room: SpaceChildInfo) {
        roomListViewModel.handle(RoomListAction.ShowRoomDetails(room.childRoomId, room.viaServers))
    }

    override fun onRejectRoomInvitation(room: RoomSummary) {
        notificationDrawerManager.updateEvents { it.clearMemberShipNotificationForRoom(room.roomId) }
        roomListViewModel.handle(RoomListAction.RejectInvitation(room))
    }
}
