

package im.vector.app.features

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.extensions.startSyncing
import im.vector.app.core.extensions.vectorStore
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.core.utils.deleteAllFiles
import im.vector.app.databinding.ActivityMainBinding
import im.vector.app.features.analytics.VectorAnalytics
import im.vector.app.features.home.HomeActivity
import im.vector.app.features.home.ShortcutsHandler
import im.vector.app.features.login.LoginConfig
import im.vector.app.features.notifications.NotificationDrawerManager
import im.vector.app.features.pin.PinCodeStore
import im.vector.app.features.pin.PinLocker
import im.vector.app.features.pin.UnlockedActivity
import im.vector.app.features.popup.PopupAlertManager
import im.vector.app.features.session.VectorSessionStore
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.signout.hard.SignedOutActivity
import im.vector.app.features.themes.ActivityOtherThemes
import im.vector.app.features.ui.UiStateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.failure.GlobalError
import timber.log.Timber
import javax.inject.Inject

@Parcelize
data class MainActivityArgs(
        val clearCache: Boolean = false,
        val clearCredentials: Boolean = false,
        val isUserLoggedOut: Boolean = false,
        val isAccountDeactivated: Boolean = false,
        val isSoftLogout: Boolean = false,
        val isGoLogin: Boolean = false
) : Parcelable


@AndroidEntryPoint
class MainActivity : VectorBaseActivity<ActivityMainBinding>(), UnlockedActivity {

    companion object {
        private const val EXTRA_ARGS = "EXTRA_ARGS"

        
        fun restartApp(activity: Activity, args: MainActivityArgs) {
            val intent = Intent(activity, MainActivity::class.java)

            intent.putExtra(EXTRA_ARGS, args)
            activity.startActivity(intent)
        }
        fun restartApp2(activity: Activity, args: MainActivityArgs) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            intent.putExtra(EXTRA_ARGS, args)
            activity.startActivity(intent)
        }
    }

    override fun getBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun getOtherThemes() = ActivityOtherThemes.Launcher

    private lateinit var args: MainActivityArgs

    @Inject lateinit var notificationDrawerManager: NotificationDrawerManager
    @Inject lateinit var sessionHolder: ActiveSessionHolder
    @Inject lateinit var errorFormatter: ErrorFormatter
    @Inject lateinit var vectorPreferences: VectorPreferences
    @Inject lateinit var uiStateRepository: UiStateRepository
    @Inject lateinit var shortcutsHandler: ShortcutsHandler
    @Inject lateinit var pinCodeStore: PinCodeStore
    @Inject lateinit var pinLocker: PinLocker
    @Inject lateinit var popupAlertManager: PopupAlertManager
    @Inject lateinit var vectorAnalytics: VectorAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args = parseArgs()
        if (args.clearCredentials || args.isUserLoggedOut || args.clearCache) {
            clearNotifications()
        }
        
        if (args.clearCache || args.clearCredentials) {
            doCleanUp()
        } else {
            startNextActivityAndFinish()
        }
    }

    private fun clearNotifications() {
        
        notificationDrawerManager.clearAllEvents()

        
        shortcutsHandler.clearShortcuts()

        
        popupAlertManager.cancelAll()
    }

    private fun parseArgs(): MainActivityArgs {
        val argsFromIntent: MainActivityArgs? = intent.getParcelableExtra(EXTRA_ARGS)
        Timber.w("Starting MainActivity with $argsFromIntent")

        return MainActivityArgs(
                clearCache = argsFromIntent?.clearCache ?: false,
                clearCredentials = argsFromIntent?.clearCredentials ?: false,
                isUserLoggedOut = argsFromIntent?.isUserLoggedOut ?: false,
                isAccountDeactivated = argsFromIntent?.isAccountDeactivated ?: false,
                isSoftLogout = argsFromIntent?.isSoftLogout ?: false
        )
    }

    private fun doCleanUp() {
        val session = sessionHolder.getSafeActiveSession()
        if (session == null) {
            startNextActivityAndFinish()
            return
        }

        val onboardingStore = session.vectorStore(this)
        when {
            args.isAccountDeactivated -> {
                lifecycleScope.launch {
                    
                    Timber.w("Account deactivated, start app")
                    sessionHolder.clearActiveSession()
                    doLocalCleanup(clearPreferences = true, onboardingStore)
                    startNextActivityAndFinish()
                }
            }
            args.clearCredentials     -> {
                lifecycleScope.launch {
                    try {
                        session.signOut(!args.isUserLoggedOut)
                    } catch (failure: Throwable) {
                        failure.printStackTrace()
                    }
                    Timber.w("SIGN_OUT: success, start app")
                    sessionHolder.clearActiveSession()
                    doLocalCleanup(clearPreferences = true, onboardingStore)
                    startNextActivityAndFinish()
                }
            }
            args.clearCache           -> {
                lifecycleScope.launch {
                    session.clearCache()
                    doLocalCleanup(clearPreferences = false, onboardingStore)
                    session.startSyncing(applicationContext)
                    startNextActivityAndFinish()
                }
            }
        }
    }

    override fun handleInvalidToken(globalError: GlobalError.InvalidToken) {
        
        Timber.w("Ignoring invalid token global error")
    }

    private suspend fun doLocalCleanup(clearPreferences: Boolean, vectorSessionStore: VectorSessionStore) {
        
        Glide.get(this@MainActivity).clearMemory()

        if (clearPreferences) {
            vectorPreferences.clearPreferences()
            uiStateRepository.reset()
            pinLocker.unlock()
            pinCodeStore.deleteEncodedPin()
            vectorAnalytics.onSignOut()
            vectorSessionStore.clear()
        }
        withContext(Dispatchers.IO) {
            
            Glide.get(this@MainActivity).clearDiskCache()

            
            deleteAllFiles(this@MainActivity.cacheDir)
        }
    }

    private fun displayError(failure: Throwable) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.dialog_title_error)
                    .setMessage(errorFormatter.toHumanReadable(failure))
                    .setPositiveButton(R.string.global_retry) { _, _ -> doCleanUp() }
                    .setNegativeButton(R.string.action_cancel) { _, _ -> startNextActivityAndFinish(ignoreClearCredentials = true) }
                    .setCancelable(false)
                    .show()
        }
    }

    private fun startNextActivityAndFinish(ignoreClearCredentials: Boolean = false) {
        val intent = when {
            args.isGoLogin                                               -> {
                navigator.openLogin(this, null)
                null
            }
            args.clearCredentials &&
                    !ignoreClearCredentials &&
                    (!args.isUserLoggedOut || args.isAccountDeactivated) -> {
                
                navigator.openLogin(this, LoginConfig(isLogOut = true))
                null
            }
            args.isSoftLogout                                            -> {
                
                navigator.softLogout(this)
                null
            }
            args.isUserLoggedOut                                         ->
                
                SignedOutActivity.newIntent(this)
            sessionHolder.hasActiveSession()                             ->
                
                
                if (sessionHolder.getActiveSession().isOpenable) {
                    HomeActivity.newIntent(this, existingSession = true)
                } else {
                    
                    navigator.softLogout(this)
                    null
                }
            else                                                         -> {
                
                navigator.openLogin(this, null)
                null
            }
        }
        intent?.let { startActivity(it) }
        finish()
    }
}
