

package im.vector.app.features.crypto.recover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.airbnb.mvrx.parentFragmentViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.databinding.FragmentBootstrapSetupRecoveryBinding
import javax.inject.Inject

class BootstrapSetupRecoveryKeyFragment @Inject constructor() :
        VectorBaseFragmentHost<FragmentBootstrapSetupRecoveryBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBootstrapSetupRecoveryBinding {
        return FragmentBootstrapSetupRecoveryBinding.inflate(inflater, container, false)
    }

    val sharedViewModel: BootstrapSharedViewModel by parentFragmentViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
        views.bootstrapSetupSecureSubmit.views.bottomSheetActionClickableZone.debouncedClicks {
            sharedViewModel.handle(BootstrapActions.StartKeyBackupMigration)
        }

        
        views.bootstrapSetupSecureUseSecurityKey.views.bottomSheetActionClickableZone.debouncedClicks {
            sharedViewModel.handle(BootstrapActions.Start(userWantsToEnterPassphrase = false))
        }
        views.bootstrapSetupSecureUseSecurityPassphrase.views.bottomSheetActionClickableZone.debouncedClicks {
            sharedViewModel.handle(BootstrapActions.Start(userWantsToEnterPassphrase = true))
        }
    }

    override fun invalidate() = withState(sharedViewModel) { state ->
        if (state.step is BootstrapStep.FirstForm) {
            if (state.step.keyBackUpExist) {
                
                views.bootstrapSetupSecureSubmit.isVisible = true
                views.bootstrapSetupSecureUseSecurityKey.isVisible = false
                views.bootstrapSetupSecureUseSecurityPassphrase.isVisible = false
                views.bootstrapSetupSecureUseSecurityPassphraseSeparator.isVisible = false
            } else {
                if (state.step.reset) {
                    views.bootstrapSetupSecureText.text = getString(R.string.reset_secure_backup_title)
                    views.bootstrapSetupWarningTextView.isVisible = true
                } else {
                    views.bootstrapSetupSecureText.text = getString(R.string.bottom_sheet_setup_secure_backup_subtitle)
                    views.bootstrapSetupWarningTextView.isVisible = false
                }
                
                views.bootstrapSetupSecureSubmit.isVisible = false
                views.bootstrapSetupSecureUseSecurityKey.isVisible = false
                views.bootstrapSetupSecureUseSecurityPassphrase.isVisible = true
                views.bootstrapSetupSecureUseSecurityPassphraseSeparator.isVisible = false
            }
        }
    }
}
