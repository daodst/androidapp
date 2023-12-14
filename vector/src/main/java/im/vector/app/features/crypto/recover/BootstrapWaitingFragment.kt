

package im.vector.app.features.crypto.recover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.airbnb.mvrx.parentFragmentViewModel
import com.airbnb.mvrx.withState
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.databinding.FragmentBootstrapWaitingBinding
import javax.inject.Inject

class BootstrapWaitingFragment @Inject constructor() :
        VectorBaseFragmentHost<FragmentBootstrapWaitingBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBootstrapWaitingBinding {
        return FragmentBootstrapWaitingBinding.inflate(inflater, container, false)
    }

    val sharedViewModel: BootstrapSharedViewModel by parentFragmentViewModel()

    override fun invalidate() = withState(sharedViewModel) { state ->
        when (state.step) {
            is BootstrapStep.Initializing -> {
                views.bootstrapLoadingStatusText.isVisible = true
                views.bootstrapDescriptionText.isVisible = true
                views.bootstrapLoadingStatusText.text = state.initializationWaitingViewData?.message
            }
            else                          -> {
                
                views.bootstrapLoadingStatusText.isVisible = false
                views.bootstrapDescriptionText.isVisible = false
            }
        }
    }
}
