

package im.vector.app.features.spaces.explore

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.R
import im.vector.app.core.extensions.cleanup
import im.vector.app.core.extensions.configureWith
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.platform.OnBackPressed
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.utils.colorizeMatchingText
import im.vector.app.core.utils.isValidUrl
import im.vector.app.core.utils.openUrlInExternalBrowser
import im.vector.app.databinding.FragmentSpaceDirectoryBinding
import im.vector.app.features.analytics.plan.MobileScreen
import im.vector.app.features.home.room.detail.timeline.TimelineEventController
import im.vector.app.features.matrixto.SpaceCardRenderer
import im.vector.app.features.permalink.PermalinkHandler
import im.vector.app.features.spaces.manage.ManageType
import im.vector.app.features.spaces.manage.SpaceAddRoomSpaceChooserBottomSheet
import im.vector.app.features.spaces.manage.SpaceManageActivity
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo
import java.net.URL
import javax.inject.Inject

@Parcelize
data class SpaceDirectoryArgs(
        val spaceId: String
) : Parcelable

class SpaceDirectoryFragment @Inject constructor(
        private val epoxyController: SpaceDirectoryController,
        private val permalinkHandler: PermalinkHandler,
        private val spaceCardRenderer: SpaceCardRenderer,
        private val colorProvider: ColorProvider
) : VectorBaseFragment<FragmentSpaceDirectoryBinding>(),
        SpaceDirectoryController.InteractionListener,
        TimelineEventController.UrlClickCallback,
        OnBackPressed {

    override fun getMenuRes() = R.menu.menu_space_directory

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentSpaceDirectoryBinding.inflate(layoutInflater, container, false)

    private val viewModel by activityViewModel(SpaceDirectoryViewModel::class)
    private val epoxyVisibilityTracker = EpoxyVisibilityTracker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsScreenName = MobileScreen.ScreenName.SpaceExploreRooms
        childFragmentManager.setFragmentResultListener(SpaceAddRoomSpaceChooserBottomSheet.REQUEST_KEY, this) { _, bundle ->

            bundle.getString(SpaceAddRoomSpaceChooserBottomSheet.BUNDLE_KEY_ACTION)?.let { action ->
                val spaceId = withState(viewModel) { it.spaceId }
                when (action) {
                    SpaceAddRoomSpaceChooserBottomSheet.ACTION_ADD_ROOMS   -> {
                        addExistingRoomActivityResult.launch(SpaceManageActivity.newIntent(requireContext(), spaceId, ManageType.AddRooms))
                    }
                    SpaceAddRoomSpaceChooserBottomSheet.ACTION_ADD_SPACES  -> {
                        addExistingRoomActivityResult.launch(SpaceManageActivity.newIntent(requireContext(), spaceId, ManageType.AddRoomsOnlySpaces))
                    }
                    SpaceAddRoomSpaceChooserBottomSheet.ACTION_CREATE_ROOM -> {
                        viewModel.handle(SpaceDirectoryViewAction.CreateNewRoom)
                    }
                    else                                                   -> {
                        
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(views.toolbar)
                .allowBack()

        epoxyController.listener = this
        views.spaceDirectoryList.configureWith(epoxyController)
        epoxyVisibilityTracker.attach(views.spaceDirectoryList)

        viewModel.onEach(SpaceDirectoryState::canAddRooms) {
            invalidateOptionsMenu()
        }

        views.addOrCreateChatRoomButton.debouncedClicks {
            withState(viewModel) {
                addExistingRooms(it.spaceId)
            }
        }

        views.spaceCard.matrixToCardMainButton.isVisible = false
        views.spaceCard.matrixToCardSecondaryButton.isVisible = false

        
        views.spaceDirectoryList.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        views.addOrCreateChatRoomButton.removeCallbacks(showFabRunnable)

                        when (newState) {
                            RecyclerView.SCROLL_STATE_IDLE     -> {
                                views.addOrCreateChatRoomButton.postDelayed(showFabRunnable, 250)
                            }
                            RecyclerView.SCROLL_STATE_DRAGGING,
                            RecyclerView.SCROLL_STATE_SETTLING -> {
                                views.addOrCreateChatRoomButton.hide()
                            }
                        }
                    }
                })
    }

    override fun onDestroyView() {
        epoxyController.listener = null
        epoxyVisibilityTracker.detach(views.spaceDirectoryList)
        views.spaceDirectoryList.cleanup()
        super.onDestroyView()
    }

    private val showFabRunnable = Runnable {
        if (isAdded) {
            views.addOrCreateChatRoomButton.show()
        }
    }

    override fun invalidate() = withState(viewModel) { state ->
        epoxyController.setData(state)

        val currentParentId = state.hierarchyStack.lastOrNull()

        if (currentParentId == null) {
            
            toolbar?.setTitle(R.string.space_explore_activity_title)
        } else {
            toolbar?.title = state.currentRootSummary?.name
                    ?: state.currentRootSummary?.canonicalAlias
                            ?: getString(R.string.space_explore_activity_title)
        }

        spaceCardRenderer.render(state.currentRootSummary, emptyList(), this, views.spaceCard, showDescription = false)
        views.addOrCreateChatRoomButton.isVisible = state.canAddRooms
    }

    override fun onPrepareOptionsMenu(menu: Menu) = withState(viewModel) { state ->
        menu.findItem(R.id.spaceAddRoom)?.isVisible = state.canAddRooms
        menu.findItem(R.id.spaceCreateRoom)?.isVisible = false 
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.spaceAddRoom    -> {
                withState(viewModel) { state ->
                    addExistingRooms(state.spaceId)
                }
                return true
            }
            R.id.spaceCreateRoom -> {
                
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onButtonClick(spaceChildInfo: SpaceChildInfo) {
        viewModel.handle(SpaceDirectoryViewAction.JoinOrOpen(spaceChildInfo))
    }

    override fun onSpaceChildClick(spaceChildInfo: SpaceChildInfo) {
        viewModel.handle(SpaceDirectoryViewAction.ExploreSubSpace(spaceChildInfo))
    }

    override fun onRoomClick(spaceChildInfo: SpaceChildInfo) {
        viewModel.handle(SpaceDirectoryViewAction.ShowDetails(spaceChildInfo))
    }

    override fun onBackPressed(toolbarButton: Boolean): Boolean {
        viewModel.handle(SpaceDirectoryViewAction.HandleBack)
        return true
    }

    override fun retry() {
        viewModel.handle(SpaceDirectoryViewAction.Retry)
    }

    private val addExistingRoomActivityResult = registerStartForActivityResult { _ ->
        viewModel.handle(SpaceDirectoryViewAction.Retry)
    }

    override fun addExistingRooms(spaceId: String) {
        SpaceAddRoomSpaceChooserBottomSheet.newInstance().show(childFragmentManager, "SpaceAddRoomSpaceChooserBottomSheet")
    }

    override fun loadAdditionalItemsIfNeeded() {
        viewModel.handle(SpaceDirectoryViewAction.LoadAdditionalItemsIfNeeded)
    }

    override fun onUrlClicked(url: String, title: String): Boolean {
        viewLifecycleOwner.lifecycleScope.launch {
            val isHandled = permalinkHandler.launch(requireActivity(), url, null)
            if (!isHandled) {
                if (title.isValidUrl() && url.isValidUrl() && URL(title).host != URL(url).host) {
                    MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_Vector_MaterialAlertDialog_Destructive)
                            .setTitle(R.string.external_link_confirmation_title)
                            .setMessage(
                                    getString(R.string.external_link_confirmation_message, title, url)
                                            .toSpannable()
                                            .colorizeMatchingText(url, colorProvider.getColorFromAttribute(R.attr.vctr_content_tertiary))
                                            .colorizeMatchingText(title, colorProvider.getColorFromAttribute(R.attr.vctr_content_tertiary))
                            )
                            .setPositiveButton(R.string._continue) { _, _ ->
                                openUrlInExternalBrowser(requireContext(), url)
                            }
                            .setNegativeButton(R.string.action_cancel, null)
                            .show()
                } else {
                    
                    openUrlInExternalBrowser(requireContext(), url)
                }
            }
        }
        
        return true
    }

    override fun onUrlLongClicked(url: String): Boolean {
        
        return false
    }
}
