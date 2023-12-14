
package im.vector.app.features.crypto.keysbackup.settings

import android.content.Context
import android.content.Intent
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.viewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.extensions.replaceFragment
import im.vector.app.core.platform.SimpleFragmentActivity
import im.vector.app.core.platform.WaitingViewData

@AndroidEntryPoint
class KeysBackupManageActivity : SimpleFragmentActivity() {

    companion object {

        fun intent(context: Context): Intent {
            return Intent(context, KeysBackupManageActivity::class.java)
        }
    }

    override fun getTitleRes() = R.string.encryption_message_recovery

    private val viewModel: KeysBackupSettingsViewModel by viewModel()

    override fun initUiAndData() {
        super.initUiAndData()
        if (supportFragmentManager.fragments.isEmpty()) {
            replaceFragment(views.container, KeysBackupSettingsFragment::class.java)
            viewModel.handle(KeyBackupSettingsAction.Init)
        }

        
        viewModel.onEach(KeysBackupSettingViewState::deleteBackupRequest) { asyncDelete ->
            when (asyncDelete) {
                is Fail    -> {
                    updateWaitingView(null)

                    MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.unknown_error)
                            .setMessage(getString(R.string.keys_backup_get_version_error, asyncDelete.error.localizedMessage))
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, null)
                            .show()
                }
                is Loading -> {
                    updateWaitingView(WaitingViewData(getString(R.string.keys_backup_settings_deleting_backup)))
                }
                else       -> {
                    updateWaitingView(null)
                }
            }
        }
    }

    override fun onBackPressed() {
        
        
        if (viewModel.canExit()) {
            finish()
            return
        }
        super.onBackPressed()
    }
}
