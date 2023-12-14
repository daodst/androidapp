

package im.vector.app.features.crypto.keysbackup.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.utils.LiveEvent
import im.vector.app.databinding.FragmentKeysBackupSetupStep1Binding
import javax.inject.Inject

class KeysBackupSetupStep1Fragment @Inject constructor() : VectorBaseFragment<FragmentKeysBackupSetupStep1Binding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentKeysBackupSetupStep1Binding {
        return FragmentKeysBackupSetupStep1Binding.inflate(inflater, container, false)
    }

    private lateinit var viewModel: KeysBackupSetupSharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activityViewModelProvider.get(KeysBackupSetupSharedViewModel::class.java)

        viewModel.showManualExport.observe(viewLifecycleOwner) {
            val showOption = it ?: false
            
            views.keysBackupSetupStep1AdvancedOptionText.visibility = if (showOption) View.VISIBLE else View.GONE
            views.keysBackupSetupStep1ManualExportButton.visibility = if (showOption) View.VISIBLE else View.GONE
        }

        views.keysBackupSetupStep1Button.debouncedClicks { onButtonClick() }
        views.keysBackupSetupStep1ManualExportButton.debouncedClicks { onManualExportClick() }
    }

    private fun onButtonClick() {
        viewModel.navigateEvent.value = LiveEvent(KeysBackupSetupSharedViewModel.NAVIGATE_TO_STEP_2)
    }

    private fun onManualExportClick() {
        viewModel.navigateEvent.value = LiveEvent(KeysBackupSetupSharedViewModel.NAVIGATE_MANUAL_EXPORT)
    }
}
