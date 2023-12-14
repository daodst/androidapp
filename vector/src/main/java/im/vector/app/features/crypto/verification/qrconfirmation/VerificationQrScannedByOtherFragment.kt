
package im.vector.app.features.crypto.verification.qrconfirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.parentFragmentViewModel
import com.airbnb.mvrx.withState
import im.vector.app.core.extensions.cleanup
import im.vector.app.core.extensions.configureWith
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.databinding.BottomSheetVerificationChildFragmentBinding
import im.vector.app.features.crypto.verification.VerificationAction
import im.vector.app.features.crypto.verification.VerificationBottomSheetViewModel
import javax.inject.Inject

class VerificationQrScannedByOtherFragment @Inject constructor(
        val controller: VerificationQrScannedByOtherController
) : VectorBaseFragmentHost<BottomSheetVerificationChildFragmentBinding>(),
        VerificationQrScannedByOtherController.Listener {

    private val sharedViewModel by parentFragmentViewModel(VerificationBottomSheetViewModel::class)

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): BottomSheetVerificationChildFragmentBinding {
        return BottomSheetVerificationChildFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun invalidate() = withState(sharedViewModel) { state ->
        controller.update(state)
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

    override fun onUserConfirmsQrCodeScanned() {
        sharedViewModel.handle(VerificationAction.OtherUserScannedSuccessfully)
    }

    override fun onUserDeniesQrCodeScanned() {
        sharedViewModel.handle(VerificationAction.OtherUserDidNotScanned)
    }
}
