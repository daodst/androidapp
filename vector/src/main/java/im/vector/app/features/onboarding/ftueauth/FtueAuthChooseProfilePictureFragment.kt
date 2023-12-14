

package im.vector.app.features.onboarding.ftueauth

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.dialogs.GalleryOrCameraDialogHelper
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.core.resources.ColorProvider
import im.vector.app.databinding.FragmentFtueProfilePictureBinding
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.onboarding.OnboardingAction
import im.vector.app.features.onboarding.OnboardingViewEvents
import im.vector.app.features.onboarding.OnboardingViewState
import org.matrix.android.sdk.api.util.MatrixItem
import javax.inject.Inject

class FtueAuthChooseProfilePictureFragment @Inject constructor(
        private val activeSessionHolder: ActiveSessionHolder,
        colorProvider: ColorProvider
) : AbstractFtueAuthFragment<FragmentFtueProfilePictureBinding>(), GalleryOrCameraDialogHelper.Listener {

    private val galleryOrCameraDialogHelper = GalleryOrCameraDialogHelper(this, colorProvider)
    private val avatarRenderer: AvatarRenderer by lazy { requireContext().singletonEntryPoint().avatarRenderer() }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFtueProfilePictureBinding {
        return FragmentFtueProfilePictureBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        views.profilePictureToolbar.setNavigationOnClickListener {
            viewModel.handle(OnboardingAction.PostViewEvent(OnboardingViewEvents.OnBack))
        }
        views.changeProfilePictureButton.debouncedClicks { galleryOrCameraDialogHelper.show() }
        views.profilePictureView.debouncedClicks { galleryOrCameraDialogHelper.show() }

        views.profilePictureSubmit.debouncedClicks {
            withState(viewModel) {
                viewModel.handle(OnboardingAction.SaveSelectedProfilePicture)
            }
        }

        views.profilePictureSkip.debouncedClicks { viewModel.handle(OnboardingAction.UpdateProfilePictureSkipped) }
    }

    override fun updateWithState(state: OnboardingViewState) {
        views.profilePictureToolbar.isInvisible = !state.personalizationState.supportsChangingDisplayName

        val hasSetPicture = state.personalizationState.selectedPictureUri != null
        views.profilePictureSubmit.isEnabled = hasSetPicture
        views.changeProfilePictureIcon.setImageResource(if (hasSetPicture) R.drawable.ic_edit else R.drawable.ic_camera_plain)

        val session = activeSessionHolder.getActiveSession()
        val matrixItem = MatrixItem.UserItem(
                id = session.myUserId,
                displayName = state.personalizationState.displayName ?: ""
        )
        avatarRenderer.render(matrixItem, localUri = state.personalizationState.selectedPictureUri, imageView = views.profilePictureView)
    }

    override fun onImageReady(uri: Uri?) {
        if (uri == null) {
            Toast.makeText(requireContext(), "Cannot retrieve cropped value", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.handle(OnboardingAction.ProfilePictureSelected(uri))
        }
    }

    override fun resetViewModel() {
        
    }

    override fun onBackPressed(toolbarButton: Boolean): Boolean {
        return when (withState(viewModel) { it.personalizationState.supportsChangingDisplayName }) {
            true  -> super.onBackPressed(toolbarButton)
            false -> {
                viewModel.handle(OnboardingAction.PostViewEvent(OnboardingViewEvents.OnTakeMeHome))
                true
            }
        }
    }
}
