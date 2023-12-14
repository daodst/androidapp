

package im.vector.app.features.matrixto

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.Incomplete
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.extensions.commitTransaction
import im.vector.app.core.platform.VectorBaseBottomSheetDialogFragment
import im.vector.app.databinding.BottomSheetMatrixToCardBinding
import im.vector.app.features.home.AvatarRenderer
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.session.permalinks.PermalinkData
import javax.inject.Inject
import kotlin.reflect.KClass

@AndroidEntryPoint
class MatrixToBottomSheet :
        VectorBaseBottomSheetDialogFragment<BottomSheetMatrixToCardBinding>() {

    @Parcelize
    data class MatrixToArgs(
            val matrixToLink: String
    ) : Parcelable

    @Inject lateinit var avatarRenderer: AvatarRenderer

    var interactionListener: InteractionListener? = null

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): BottomSheetMatrixToCardBinding {
        return BottomSheetMatrixToCardBinding.inflate(inflater, container, false)
    }

    private val viewModel by fragmentViewModel(MatrixToBottomSheetViewModel::class)

    interface InteractionListener {
        fun mxToBottomSheetNavigateToRoom(roomId: String)
        fun mxToBottomSheetSwitchToSpace(spaceId: String)
    }

    override fun invalidate() = withState(viewModel) { state ->
        super.invalidate()
        when (state.linkType) {
            is PermalinkData.RoomLink            -> {
                views.matrixToCardContentLoading.isVisible = state.roomPeekResult is Incomplete
                showFragment(MatrixToRoomSpaceFragment::class, Bundle())
            }
            is PermalinkData.UserLink            -> {
                views.matrixToCardContentLoading.isVisible = state.matrixItem is Incomplete
                showFragment(MatrixToUserFragment::class, Bundle())
            }
            is PermalinkData.GroupLink           -> Unit
            is PermalinkData.FallbackLink        -> Unit
            is PermalinkData.RoomEmailInviteLink -> Unit
        }
    }

    private fun showFragment(fragmentClass: KClass<out Fragment>, bundle: Bundle) {
        if (childFragmentManager.findFragmentByTag(fragmentClass.simpleName) == null) {
            childFragmentManager.commitTransaction {
                replace(views.matrixToCardFragmentContainer.id,
                        fragmentClass.java,
                        bundle,
                        fragmentClass.simpleName
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeViewEvents {
            when (it) {
                is MatrixToViewEvents.NavigateToRoom  -> {
                    interactionListener?.mxToBottomSheetNavigateToRoom(it.roomId)
                    dismiss()
                }
                MatrixToViewEvents.Dismiss            -> dismiss()
                is MatrixToViewEvents.NavigateToSpace -> {
                    interactionListener?.mxToBottomSheetSwitchToSpace(it.spaceId)
                    dismiss()
                }
                is MatrixToViewEvents.ShowModalError  -> {
                    MaterialAlertDialogBuilder(requireContext())
                            .setMessage(it.error)
                            .setPositiveButton(getString(R.string.ok), null)
                            .show()
                }
            }
        }
    }

    companion object {
        fun withLink(matrixToLink: String): MatrixToBottomSheet {
            return MatrixToBottomSheet().apply {
                setArguments(MatrixToArgs(matrixToLink = matrixToLink))
            }
        }
    }
}
