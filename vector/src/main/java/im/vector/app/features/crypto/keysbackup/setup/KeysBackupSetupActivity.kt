
package im.vector.app.features.crypto.keysbackup.setup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.dialogs.ExportKeysDialog
import im.vector.app.core.extensions.observeEvent
import im.vector.app.core.extensions.queryExportKeys
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.extensions.replaceFragment
import im.vector.app.core.platform.SimpleFragmentActivity
import im.vector.app.core.utils.toast
import im.vector.app.features.crypto.keys.KeysExporter
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KeysBackupSetupActivity : SimpleFragmentActivity() {

    override fun getTitleRes() = R.string.title_activity_keys_backup_setup

    private lateinit var viewModel: KeysBackupSetupSharedViewModel

    @Inject lateinit var keysExporter: KeysExporter
    @Inject lateinit var activeSessionHolder: ActiveSessionHolder

    private val session by lazy {
        activeSessionHolder.getActiveSession()
    }

    override fun initUiAndData() {
        super.initUiAndData()
        if (isFirstCreation()) {
            replaceFragment(views.container, KeysBackupSetupStep1Fragment::class.java)
        }

        viewModel = viewModelProvider.get(KeysBackupSetupSharedViewModel::class.java)
        viewModel.showManualExport.value = intent.getBooleanExtra(EXTRA_SHOW_MANUAL_EXPORT, false)

        intent.getStringExtra(MANUAL_PRIVATEKEY)?.takeIf { !TextUtils.isEmpty(it) }?.let {
            viewModel.privatekey = it
        }

        viewModel.initSession(session)

        viewModel.isCreatingBackupVersion.observe(this) {
            val isCreating = it ?: false
            if (isCreating) {
                showWaitingView()
            } else {
                hideWaitingView()
            }
        }

        viewModel.loadingStatus.observe(this) {
            it?.let {
                updateWaitingView(it)
            }
        }

        viewModel.navigateEvent.observeEvent(this) { uxStateEvent ->
            when (uxStateEvent) {
                KeysBackupSetupSharedViewModel.NAVIGATE_TO_STEP_2      -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    replaceFragment(views.container, KeysBackupSetupStep2Fragment::class.java)
                }
                KeysBackupSetupSharedViewModel.NAVIGATE_TO_STEP_3      -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    replaceFragment(views.container, KeysBackupSetupStep3Fragment::class.java)
                }
                KeysBackupSetupSharedViewModel.NAVIGATE_FINISH         -> {
                    val resultIntent = Intent()
                    viewModel.keysVersion.value?.version?.let {
                        resultIntent.putExtra(KEYS_VERSION, it)
                    }
                    viewModel.privatekey.takeIf { !TextUtils.isEmpty(it) }?.let {
                        resultIntent.putExtra(MANUAL_PRIVATEKEY, it)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                KeysBackupSetupSharedViewModel.NAVIGATE_PROMPT_REPLACE -> {
                    MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.keys_backup_setup_override_backup_prompt_tile)
                            .setMessage(R.string.keys_backup_setup_override_backup_prompt_description)
                            .setPositiveButton(R.string.keys_backup_setup_override_replace) { _, _ ->
                                viewModel.forceCreateKeyBackup(this)
                            }.setNegativeButton(R.string.keys_backup_setup_override_stop) { _, _ ->
                                viewModel.stopAndKeepAfterDetectingExistingOnServer()
                            }
                            .show()
                }
                KeysBackupSetupSharedViewModel.NAVIGATE_MANUAL_EXPORT  -> {
                    queryExportKeys(session.myUserId, saveStartForActivityResult)
                }
            }
        }

        viewModel.prepareRecoverFailError.observe(this) { error ->
            if (error != null) {
                MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.unknown_error)
                        .setMessage(error.localizedMessage)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            
                            viewModel.prepareRecoverFailError.value = null
                        }
                        .show()
            }
        }

        viewModel.creatingBackupError.observe(this) { error ->
            if (error != null) {
                MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.unexpected_error)
                        .setMessage(error.localizedMessage)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            
                            viewModel.creatingBackupError.value = null
                        }
                        .show()
            }
        }
    }

    private val saveStartForActivityResult = registerStartForActivityResult { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val uri = activityResult.data?.data
            if (uri != null) {
                ExportKeysDialog().show(this, object : ExportKeysDialog.ExportKeyDialogListener {
                    override fun onPassphrase(passphrase: String) {
                        showWaitingView()
                        export(passphrase, uri)
                    }
                })
            } else {
                toast(getString(R.string.unexpected_error))
                hideWaitingView()
            }
        }
    }

    private fun export(passphrase: String, uri: Uri) {
        lifecycleScope.launch {
            try {
                keysExporter.export(passphrase, uri)
                toast(getString(R.string.encryption_exported_successfully))
                setResult(Activity.RESULT_OK, Intent().apply { putExtra(MANUAL_EXPORT, true) })
                finish()
            } catch (failure: Throwable) {
                toast(failure.localizedMessage ?: getString(R.string.unexpected_error))
            }
            hideWaitingView()
        }
    }

    override fun onBackPressed() {
        if (viewModel.shouldPromptOnBack) {
            if (waitingView?.isVisible == true) {
                return
            }
            MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.keys_backup_setup_skip_title)
                    .setMessage(R.string.keys_backup_setup_skip_msg)
                    .setNegativeButton(R.string.action_cancel, null)
                    .setPositiveButton(R.string.action_leave) { _, _ ->
                        finish()
                    }
                    .show()
        } else {
            super.onBackPressed()
        }
    }



    companion object {
        const val KEYS_VERSION = "KEYS_VERSION"
        const val MANUAL_EXPORT = "MANUAL_EXPORT"
        const val MANUAL_PRIVATEKEY = "MANUAL_PRIVATEKEY"
        const val EXTRA_SHOW_MANUAL_EXPORT = "SHOW_MANUAL_EXPORT"

        fun intent(context: Context, showManualExport: Boolean, privateKey: String? = null): Intent {
            val intent = Intent(context, KeysBackupSetupActivity::class.java)
            intent.putExtra(EXTRA_SHOW_MANUAL_EXPORT, showManualExport)
            privateKey?.let {
                intent.putExtra(MANUAL_PRIVATEKEY, it)
            }

            return intent
        }
    }
}
