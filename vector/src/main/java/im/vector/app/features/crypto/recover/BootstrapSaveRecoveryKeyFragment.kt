

package im.vector.app.features.crypto.recover

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.parentFragmentViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.extensions.safeOpenOutputStream
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.utils.startSharePlainTextIntent
import im.vector.app.core.utils.toast
import im.vector.app.databinding.FragmentBootstrapSaveKeyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class BootstrapSaveRecoveryKeyFragment @Inject constructor(
        private val colorProvider: ColorProvider
) : VectorBaseFragmentHost<FragmentBootstrapSaveKeyBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBootstrapSaveKeyBinding {
        return FragmentBootstrapSaveKeyBinding.inflate(inflater, container, false)
    }

    val sharedViewModel: BootstrapSharedViewModel by parentFragmentViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.recoverySave.views.bottomSheetActionClickableZone.debouncedClicks { downloadRecoveryKey() }
        views.recoveryCopy.views.bottomSheetActionClickableZone.debouncedClicks { shareRecoveryKey() }
        views.recoveryContinue.views.bottomSheetActionClickableZone.debouncedClicks {
            
            
            
            sharedViewModel.handle(BootstrapActions.Completed)
        }
    }

    private fun downloadRecoveryKey() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TITLE, "element-recovery-key.txt")

        try {
            sharedViewModel.handle(BootstrapActions.SaveReqQueryStarted)
            saveStartForActivityResult.launch(Intent.createChooser(intent, getString(R.string.keys_backup_setup_step3_please_make_copy)))
        } catch (activityNotFoundException: ActivityNotFoundException) {
            requireActivity().toast(R.string.error_no_external_application_found)
            sharedViewModel.handle(BootstrapActions.SaveReqFailed)
        }
    }

    private val saveStartForActivityResult = registerStartForActivityResult { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val uri = activityResult.data?.data ?: return@registerStartForActivityResult
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    sharedViewModel.handle(BootstrapActions.SaveKeyToUri(requireContext().safeOpenOutputStream(uri)!!))
                } catch (failure: Throwable) {
                    sharedViewModel.handle(BootstrapActions.SaveReqFailed)
                }
            }
        } else {
            
            sharedViewModel.handle(BootstrapActions.SaveReqFailed)
        }
    }

    private val copyStartForActivityResult = registerStartForActivityResult { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            sharedViewModel.handle(BootstrapActions.RecoveryKeySaved)
        }
    }

    private fun shareRecoveryKey() = withState(sharedViewModel) { state ->
        val recoveryKey = state.recoveryKeyCreationInfo?.recoveryKey?.formatRecoveryKey()
                ?: return@withState

        startSharePlainTextIntent(
                this,
                copyStartForActivityResult,
                context?.getString(R.string.keys_backup_setup_step3_share_intent_chooser_title),
                recoveryKey,
                context?.getString(R.string.recovery_key)
        )
    }

    override fun invalidate() = withState(sharedViewModel) { state ->
        val step = state.step
        if (step !is BootstrapStep.SaveRecoveryKey) return@withState

        views.recoveryContinue.isVisible = step.isSaved
        views.bootstrapRecoveryKeyText.text = state.recoveryKeyCreationInfo?.recoveryKey?.formatRecoveryKey()
    }
}
