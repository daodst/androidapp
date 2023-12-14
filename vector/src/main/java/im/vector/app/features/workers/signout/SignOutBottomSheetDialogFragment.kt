

package im.vector.app.features.workers.signout

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.dialogs.ExportKeysDialog
import im.vector.app.core.extensions.queryExportKeys
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.extensions.takeAs
import im.vector.app.core.platform.VectorBaseBottomSheetDialogFragment
import im.vector.app.core.utils.toast
import im.vector.app.databinding.BottomSheetLogoutAndBackupBinding
import im.vector.app.features.crypto.keysbackup.setup.KeysBackupSetupActivity
import im.vector.app.features.crypto.recover.BootstrapBottomSheet
import im.vector.app.features.crypto.recover.SetupMode
import im.vector.app.features.home.getPassphrase
import im.vector.app.provide.ChatStatusProvide.getAddress
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupState

@AndroidEntryPoint
class SignOutBottomSheetDialogFragment :
        VectorBaseBottomSheetDialogFragment<BottomSheetLogoutAndBackupBinding>() {

    var onSignOut: Runnable? = null

    companion object {
        fun newInstance() = SignOutBottomSheetDialogFragment()
    }

    init {
        isCancelable = true
    }

    private val viewModel: SignoutCheckViewModel by fragmentViewModel(SignoutCheckViewModel::class)

    override fun onResume() {
        super.onResume()
        viewModel.refreshRemoteStateIfNeeded()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.setupRecoveryButton.action = {
            getWalletPrivateKey {
                BootstrapBottomSheet.show(parentFragmentManager, SetupMode.NORMAL, it)
            }

        }

        views.exitAnywayButton.action = {
            context?.let {
                MaterialAlertDialogBuilder(it)
                        .setTitle(R.string.are_you_sure)
                        .setMessage(R.string.sign_out_bottom_sheet_will_lose_secure_messages)
                        .setPositiveButton(R.string.backup, null)
                        .setNegativeButton(R.string.action_sign_out) { _, _ ->
                            onSignOut?.run()
                        }
                        .show()
            }
        }

        views.signOutButton.action = {
            onSignOut?.run()
            dismissAllowingStateLoss()
        }

        views.exportManuallyButton.action = {
            withState(viewModel) { state ->
                queryExportKeys(state.userId, manualExportKeysActivityResultLauncher)
            }
        }

        views.setupMegolmBackupButton.action = {
            getWalletPrivateKey {
                setupBackupActivityResultLauncher.launch(KeysBackupSetupActivity.intent(requireContext(), false, it))
            }

        }
    }

    private fun getWalletPrivateKey(consumer: (String) -> Unit) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                    .setTitle(getString(R.string.bottom_sheet_logout_and_backup_tips_title))
                    .setMessage(getString(R.string.bottom_sheet_logout_and_backup_tips))
                    .setPositiveButton(R.string.backup) { _, _ ->
                        vectorBaseActivity.applicationContext?.takeAs<IApplication>()?.apply {
                            getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay?.showPayDialog(
                                    vectorBaseActivity,
                                    getAddress(vectorBaseActivity),
                            ) {
                                it?.let {
                                    val passphrase = getPassphrase(it)
                                    consumer(passphrase)
                                } ?: kotlin.run {
                                    vectorBaseActivity.toast(getString(R.string.bottom_sheet_logout_and_backup_wallet_error))
                                }
                            }
                        }
                    }
                    .setNegativeButton(getString(R.string.bottom_sheet_logout_and_backup_bt_diss)) { _, _ ->
                    }
                    .show()
        }
    }

    override fun invalidate() = withState(viewModel) { state ->
        views.signoutExportingLoading.isVisible = false
        if (state.crossSigningSetupAllKeysKnown && !state.backupIsSetup) {
            views.bottomSheetSignoutWarningText.text = getString(R.string.sign_out_bottom_sheet_warning_no_backup)
            views.backingUpStatusGroup.isVisible = false
            
            views.setupRecoveryButton.isVisible = true
            views.setupMegolmBackupButton.isVisible = false
            
            views.exportManuallyButton.isVisible = false
            views.exitAnywayButton.isVisible = true
            views.signOutButton.isVisible = false
        } else if (state.keysBackupState == KeysBackupState.Unknown || state.keysBackupState == KeysBackupState.Disabled) {
            views.bottomSheetSignoutWarningText.text = getString(R.string.sign_out_bottom_sheet_warning_no_backup)
            views.backingUpStatusGroup.isVisible = false

            
            
            
            views.setupRecoveryButton.isVisible = false
            views.setupMegolmBackupButton.isVisible = true
            if (true) {
                
                
                if (!state.crossSigning) {
                    viewModel.handle(SignoutCheckViewModel.Actions.InitializeCrossSigning)
                }
                views.setupMegolmBackupButton.isVisible = false
                views.setupRecoveryButton.isVisible = true
            }
            
            views.exportManuallyButton.isVisible = false
            views.exitAnywayButton.isVisible = true
            views.signOutButton.isVisible = false
        } else {
            
            
            views.setupRecoveryButton.isVisible = false

            when (state.keysBackupState) {
                KeysBackupState.ReadyToBackUp -> {
                    views.bottomSheetSignoutWarningText.text = getString(R.string.action_sign_out_confirmation_simple)

                    
                    views.backingUpStatusGroup.isVisible = true
                    views.backupProgress.isVisible = false
                    views.backupCompleteImage.isVisible = true
                    views.backupStatusText.text = getString(R.string.keys_backup_info_keys_all_backup_up)

                    if (false) {
                        views.setupRecoveryButton.isVisible = true
                        viewModel.handle(SignoutCheckViewModel.Actions.InitializeCrossSigning)
                    }

                    views.setupMegolmBackupButton.isVisible = false
                    views.exportManuallyButton.isVisible = false
                    views.exitAnywayButton.isVisible = false
                    
                    views.signOutButton.isVisible = true
                }
                KeysBackupState.WillBackUp,
                KeysBackupState.BackingUp     -> {
                    views.bottomSheetSignoutWarningText.text = getString(R.string.sign_out_bottom_sheet_warning_backing_up)

                    
                    views.backingUpStatusGroup.isVisible = true
                    views.backupProgress.isVisible = true
                    views.backupCompleteImage.isVisible = false
                    views.backupStatusText.text = getString(R.string.sign_out_bottom_sheet_backing_up_keys)

                    views.setupMegolmBackupButton.isVisible = false
                    views.exportManuallyButton.isVisible = false
                    views.exitAnywayButton.isVisible = true
                    views.signOutButton.isVisible = false
                }
                KeysBackupState.NotTrusted    -> {
                    views.bottomSheetSignoutWarningText.text = getString(R.string.sign_out_bottom_sheet_warning_backup_not_active)
                    
                    views.backingUpStatusGroup.isVisible = false
                    if (true) {
                        viewModel.handle(SignoutCheckViewModel.Actions.DelBaks)
                    }
                    
                    views.setupMegolmBackupButton.isVisible = true
                    views.exportManuallyButton.isVisible = false
                    views.exitAnywayButton.isVisible = true
                    views.signOutButton.isVisible = false
                }
                else                          -> {
                    
                    views.exitAnywayButton.isVisible = true
                }
            }
        }
        super.invalidate()
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): BottomSheetLogoutAndBackupBinding {
        return BottomSheetLogoutAndBackupBinding.inflate(inflater, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        
        (dialog as? BottomSheetDialog)?.let { bottomSheetDialog ->
            bottomSheetDialog.setOnShowListener { dialog ->
                val d = dialog as BottomSheetDialog
                (d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout)?.let {
                    BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
        return dialog
    }

    private val manualExportKeysActivityResultLauncher = registerStartForActivityResult {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data
            if (uri != null) {
                activity?.let { activity ->
                    ExportKeysDialog().show(activity, object : ExportKeysDialog.ExportKeyDialogListener {
                        override fun onPassphrase(passphrase: String) {
                            viewModel.handle(SignoutCheckViewModel.Actions.ExportKeys(passphrase, uri))
                        }
                    })
                }
            }
        }
    }

    private val setupBackupActivityResultLauncher = registerStartForActivityResult {
        if (it.resultCode == Activity.RESULT_OK) {
            if (it.data?.getBooleanExtra(KeysBackupSetupActivity.MANUAL_EXPORT, false) == true) {
                viewModel.handle(SignoutCheckViewModel.Actions.KeySuccessfullyManuallyExported)
            }
        }
    }
}
