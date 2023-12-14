

package im.vector.app.features.spaces.create

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.activityViewModel
import im.vector.app.core.dialogs.GalleryOrCameraDialogHelper
import im.vector.app.core.extensions.configureWith
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.platform.OnBackPressed
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.resources.ColorProvider
import im.vector.app.databinding.FragmentSpaceCreateGenericEpoxyFormBinding
import javax.inject.Inject

class CreateSpaceDetailsFragment @Inject constructor(
        private val epoxyController: SpaceDetailEpoxyController,
        colorProvider: ColorProvider
) : VectorBaseFragment<FragmentSpaceCreateGenericEpoxyFormBinding>(), SpaceDetailEpoxyController.Listener,
        GalleryOrCameraDialogHelper.Listener, OnBackPressed {

    private val sharedViewModel: CreateSpaceViewModel by activityViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentSpaceCreateGenericEpoxyFormBinding.inflate(layoutInflater, container, false)

    private val galleryOrCameraDialogHelper = GalleryOrCameraDialogHelper(this, colorProvider)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.recyclerView.configureWith(epoxyController)
        epoxyController.listener = this

        sharedViewModel.onEach {
            epoxyController.setData(it)
        }

        views.nextButton.debouncedClicks {
            view.hideKeyboard()
            sharedViewModel.handle(CreateSpaceAction.NextFromDetails)
        }
    }

    override fun onImageReady(uri: Uri?) {
        sharedViewModel.handle(CreateSpaceAction.SetAvatar(uri))
    }

    
    
    

    override fun onAvatarDelete() {
        sharedViewModel.handle(CreateSpaceAction.SetAvatar(null))
    }

    override fun onAvatarChange() {
        galleryOrCameraDialogHelper.show()
    }

    override fun onNameChange(newName: String) {
        sharedViewModel.handle(CreateSpaceAction.NameChanged(newName))
    }

    override fun onTopicChange(newTopic: String) {
        sharedViewModel.handle(CreateSpaceAction.TopicChanged(newTopic))
    }

    override fun setAliasLocalPart(aliasLocalPart: String) {
        sharedViewModel.handle(CreateSpaceAction.SpaceAliasChanged(aliasLocalPart))
    }

    override fun onBackPressed(toolbarButton: Boolean): Boolean {
        sharedViewModel.handle(CreateSpaceAction.OnBackPressed)
        return true
    }
}
