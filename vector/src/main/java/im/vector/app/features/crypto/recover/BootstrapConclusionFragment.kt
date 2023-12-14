

package im.vector.app.features.crypto.recover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpannable
import com.airbnb.mvrx.parentFragmentViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.utils.colorizeMatchingText
import im.vector.app.databinding.FragmentBootstrapConclusionBinding
import javax.inject.Inject

class BootstrapConclusionFragment @Inject constructor(
        private val colorProvider: ColorProvider
) : VectorBaseFragmentHost<FragmentBootstrapConclusionBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBootstrapConclusionBinding {
        return FragmentBootstrapConclusionBinding.inflate(inflater, container, false)
    }

    val sharedViewModel: BootstrapSharedViewModel by parentFragmentViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.bootstrapConclusionContinue.views.bottomSheetActionClickableZone.debouncedClicks { sharedViewModel.handle(BootstrapActions.Completed) }
    }

    override fun invalidate() = withState(sharedViewModel) { state ->
        if (state.step !is BootstrapStep.DoneSuccess) return@withState

        views.bootstrapConclusionText.text = getString(
                R.string.bootstrap_cross_signing_success,
                getString(R.string.recovery_passphrase),
                getString(R.string.message_key)
        )
                .toSpannable()
                .colorizeMatchingText(getString(R.string.recovery_passphrase), colorProvider.getColorFromAttribute(android.R.attr.textColorLink))
                .colorizeMatchingText(getString(R.string.message_key), colorProvider.getColorFromAttribute(android.R.attr.textColorLink))
    }
}
