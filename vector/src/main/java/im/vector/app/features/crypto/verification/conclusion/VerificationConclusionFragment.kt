
package im.vector.app.features.crypto.verification.conclusion

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.parentFragmentViewModel
import com.airbnb.mvrx.withState
import im.vector.app.core.extensions.cleanup
import im.vector.app.core.extensions.configureWith
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.databinding.BottomSheetVerificationChildFragmentBinding
import im.vector.app.features.crypto.verification.VerificationAction
import im.vector.app.features.crypto.verification.VerificationBottomSheetViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

class VerificationConclusionFragment @Inject constructor(
        val controller: VerificationConclusionController
) : VectorBaseFragmentHost<BottomSheetVerificationChildFragmentBinding>(),
        VerificationConclusionController.Listener {

    @Parcelize
    data class Args(
            val isSuccessFull: Boolean,
            val cancelReason: String?,
            val isMe: Boolean
    ) : Parcelable

    private val sharedViewModel by parentFragmentViewModel(VerificationBottomSheetViewModel::class)

    private val viewModel by fragmentViewModel(VerificationConclusionViewModel::class)

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): BottomSheetVerificationChildFragmentBinding {
        return BottomSheetVerificationChildFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    override fun onDestroyView() {
        views.bottomSheetVerificationRecyclerView.cleanup()
        controller.listener = null
        super.onDestroyView()
    }

    private fun setupRecyclerView() {
        views.bottomSheetVerificationRecyclerView.configureWith(controller, hasFixedSize = false, disableItemAnimation = true)
        controller.listener = this
    }

    override fun invalidate() = withState(viewModel) { state ->
        controller.update(state)
    }

    override fun onButtonTapped() {
        sharedViewModel.handle(VerificationAction.GotItConclusion)
    }
}
