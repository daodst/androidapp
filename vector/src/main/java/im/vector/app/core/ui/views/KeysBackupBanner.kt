

package im.vector.app.core.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.core.view.isVisible
import im.vector.app.R
import im.vector.app.core.di.DefaultSharedPreferences
import im.vector.app.databinding.ViewKeysBackupBannerBinding
import timber.log.Timber


class KeysBackupBanner @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    var delegate: Delegate? = null
    private var state: State = State.Initial

    private lateinit var views: ViewKeysBackupBannerBinding

    init {
        setupView()
        DefaultSharedPreferences.getInstance(context).edit {
            putBoolean(BANNER_SETUP_DO_NOT_SHOW_AGAIN, false)
            putString(BANNER_RECOVER_DO_NOT_SHOW_FOR_VERSION, "")
        }
    }

    
    fun render(newState: State, force: Boolean = false) {
        if (newState == state && !force) {
            Timber.v("State unchanged")
            return
        }
        Timber.v("Rendering $newState")

        state = newState

        hideAll()
        when (newState) {
            State.Initial    -> renderInitial()
            State.Hidden     -> renderHidden()
            is State.Setup   -> renderSetup(newState.numberOfKeys)
            is State.Recover -> renderRecover(newState.version)
            is State.Update  -> renderUpdate(newState.version)
            State.BackingUp  -> renderBackingUp()
        }
    }

    override fun onClick(v: View?) {
        when (state) {
            is State.Setup   -> delegate?.setupKeysBackup()
            is State.Update,
            is State.Recover -> delegate?.recoverKeysBackup()
            else             -> Unit
        }
    }

    private fun onCloseClicked() {
        state.let {
            when (it) {
                is State.Setup   -> {
                    DefaultSharedPreferences.getInstance(context).edit {
                        putBoolean(BANNER_SETUP_DO_NOT_SHOW_AGAIN, true)
                    }
                }
                is State.Recover -> {
                    DefaultSharedPreferences.getInstance(context).edit {
                        putString(BANNER_RECOVER_DO_NOT_SHOW_FOR_VERSION, it.version)
                    }
                }
                is State.Update  -> {
                    DefaultSharedPreferences.getInstance(context).edit {
                        putString(BANNER_UPDATE_DO_NOT_SHOW_FOR_VERSION, it.version)
                    }
                }
                else             -> {
                    
                }
            }
        }

        
        render(state, true)
    }

    

    private fun setupView() {
        inflate(context, R.layout.view_keys_backup_banner, this)

        setOnClickListener(this)
        views = ViewKeysBackupBannerBinding.bind(this)
        views.viewKeysBackupBannerText1.setOnClickListener(this)
        views.viewKeysBackupBannerText2.setOnClickListener(this)
        views.viewKeysBackupBannerClose.setOnClickListener { onCloseClicked() }
    }

    private fun renderInitial() {
        isVisible = false
    }

    private fun renderHidden() {
        isVisible = false
    }

    private fun renderSetup(nbOfKeys: Int) {
        if (nbOfKeys == 0 ||
                DefaultSharedPreferences.getInstance(context).getBoolean(BANNER_SETUP_DO_NOT_SHOW_AGAIN, false)) {
            
            isVisible = false
        } else {
            isVisible = true

            views.viewKeysBackupBannerText1.setText(R.string.secure_backup_banner_setup_line1)
            views.viewKeysBackupBannerText2.isVisible = true
            views.viewKeysBackupBannerText2.setText(R.string.secure_backup_banner_setup_line2)
            views.viewKeysBackupBannerCloseGroup.isVisible = true
        }
    }

    private fun renderRecover(version: String) {
        if (version == DefaultSharedPreferences.getInstance(context).getString(BANNER_RECOVER_DO_NOT_SHOW_FOR_VERSION, null)) {
            isVisible = false
        } else {
            isVisible = true

            views.viewKeysBackupBannerText1.setText(R.string.keys_backup_banner_recover_line1)
            views.viewKeysBackupBannerText2.isVisible = true
            views.viewKeysBackupBannerText2.setText(R.string.keys_backup_banner_recover_line2)
            views.viewKeysBackupBannerCloseGroup.isVisible = true
        }
    }

    private fun renderUpdate(version: String) {
        if (version == DefaultSharedPreferences.getInstance(context).getString(BANNER_UPDATE_DO_NOT_SHOW_FOR_VERSION, null)) {
            isVisible = false
        } else {
            isVisible = true

            views.viewKeysBackupBannerText1.setText(R.string.keys_backup_banner_update_line1)
            views.viewKeysBackupBannerText2.isVisible = true
            views.viewKeysBackupBannerText2.setText(R.string.keys_backup_banner_update_line2)
            views.viewKeysBackupBannerCloseGroup.isVisible = true
        }
    }

    private fun renderBackingUp() {
        isVisible = true
        views.viewKeysBackupBannerText1.setText(R.string.secure_backup_banner_setup_line1)
        views.viewKeysBackupBannerText2.isVisible = true
        views.viewKeysBackupBannerText2.setText(R.string.keys_backup_banner_in_progress)
        views.viewKeysBackupBannerLoading.isVisible = true
    }

    
    private fun hideAll() {
        views.viewKeysBackupBannerText2.isVisible = false
        views.viewKeysBackupBannerCloseGroup.isVisible = false
        views.viewKeysBackupBannerLoading.isVisible = false
    }

    
    sealed class State {
        
        object Initial : State()

        
        object Hidden : State()

        
        data class Setup(val numberOfKeys: Int) : State()

        
        data class Recover(val version: String) : State()

        
        data class Update(val version: String) : State()

        
        object BackingUp : State()
    }

    
    interface Delegate {
        fun setupKeysBackup()
        fun recoverKeysBackup()
    }

    companion object {
        
        private const val BANNER_SETUP_DO_NOT_SHOW_AGAIN = "BANNER_SETUP_DO_NOT_SHOW_AGAIN"

        
        private const val BANNER_RECOVER_DO_NOT_SHOW_FOR_VERSION = "BANNER_RECOVER_DO_NOT_SHOW_FOR_VERSION"

        
        private const val BANNER_UPDATE_DO_NOT_SHOW_FOR_VERSION = "BANNER_UPDATE_DO_NOT_SHOW_FOR_VERSION"

        
        fun onRecoverDoneForVersion(context: Context, version: String) {
            DefaultSharedPreferences.getInstance(context).edit {
                putString(BANNER_RECOVER_DO_NOT_SHOW_FOR_VERSION, version)
            }
        }
    }
}
