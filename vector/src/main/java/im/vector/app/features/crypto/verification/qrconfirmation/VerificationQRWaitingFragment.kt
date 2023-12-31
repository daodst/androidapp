

package im.vector.app.features.crypto.verification.qrconfirmation

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Mavericks
import im.vector.app.core.extensions.cleanup
import im.vector.app.core.extensions.configureWith
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.databinding.BottomSheetVerificationChildFragmentBinding
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

class VerificationQRWaitingFragment @Inject constructor(
        val controller: VerificationQRWaitingController
) : VectorBaseFragmentHost<BottomSheetVerificationChildFragmentBinding>() {

    @Parcelize
    data class Args(
            val isMe: Boolean,
            val otherUserName: String
    ) : Parcelable

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): BottomSheetVerificationChildFragmentBinding {
        return BottomSheetVerificationChildFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        (arguments?.getParcelable(Mavericks.KEY_ARG) as? Args)?.let {
            controller.update(it)
        }
    }

    override fun onDestroyView() {
        views.bottomSheetVerificationRecyclerView.cleanup()
        super.onDestroyView()
    }

    private fun setupRecyclerView() {
        views.bottomSheetVerificationRecyclerView.configureWith(controller, hasFixedSize = false, disableItemAnimation = true)
    }
}
