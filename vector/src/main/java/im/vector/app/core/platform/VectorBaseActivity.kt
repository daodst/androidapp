

package im.vector.app.core.platform

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.MavericksView
import com.bumptech.glide.util.Util
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.EntryPointAccessors
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.di.ActivityEntryPoint
import im.vector.app.core.dialogs.DialogLocker
import im.vector.app.core.dialogs.UnrecognizedCertificateDialog
import im.vector.app.core.extensions.observeEvent
import im.vector.app.core.extensions.observeNotNull
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.extensions.restart
import im.vector.app.core.extensions.setTextOrHide
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.core.extensions.toMvRxBundle
import im.vector.app.core.utils.ToolbarConfig
import im.vector.app.core.utils.toast
import im.vector.app.features.MainActivity
import im.vector.app.features.MainActivityArgs
import im.vector.app.features.analytics.AnalyticsTracker
import im.vector.app.features.analytics.plan.MobileScreen
import im.vector.app.features.configuration.VectorConfiguration
import im.vector.app.features.consent.ConsentNotGivenHelper
import im.vector.app.features.navigation.Navigator
import im.vector.app.features.pin.PinLocker
import im.vector.app.features.pin.PinMode
import im.vector.app.features.pin.UnlockedActivity
import im.vector.app.features.rageshake.BugReportActivity
import im.vector.app.features.rageshake.BugReporter
import im.vector.app.features.rageshake.RageShake
import im.vector.app.features.session.SessionListener
import im.vector.app.features.settings.FontScale
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.themes.ActivityOtherThemes
import im.vector.app.features.themes.ThemeUtils
import im.vector.app.receivers.DebugReceiver
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.failure.GlobalError
import org.matrix.android.sdk.api.failure.InitialSyncRequestReason
import reactivecircus.flowbinding.android.view.clicks
import timber.log.Timber
import javax.inject.Inject

abstract class VectorBaseActivity<VB : ViewBinding> : AppCompatActivity(), MavericksView {
    

    protected var analyticsScreenName: MobileScreen.ScreenName? = null

    protected lateinit var analyticsTracker: AnalyticsTracker

    

    protected lateinit var views: VB

    

    private lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val viewModelProvider
        get() = ViewModelProvider(this, viewModelFactory)

    fun <T : VectorViewEvents> VectorViewModel<*, *, T>.observeViewEvents(observer: (T) -> Unit) {
        viewEvents
                .stream()
                .onEach {
                    hideWaitingView()
                    observer(it)
                }
                .launchIn(lifecycleScope)
    }

    var toolbar: ToolbarConfig? = null

    

    protected fun View.debouncedClicks(onClicked: () -> Unit) {
        clicks()
                .onEach { onClicked() }
                .launchIn(lifecycleScope)
    }

    

    private lateinit var configurationViewModel: ConfigurationViewModel
    private lateinit var sessionListener: SessionListener
    protected lateinit var bugReporter: BugReporter
    private lateinit var pinLocker: PinLocker

    @Inject
    lateinit var rageShake: RageShake
    lateinit var navigator: Navigator
        private set
    private lateinit var fragmentFactory: FragmentFactory

    private lateinit var activeSessionHolder: ActiveSessionHolder
    private lateinit var vectorPreferences: VectorPreferences

    
    private var mainActivityStarted = false

    private var savedInstanceState: Bundle? = null

    
    private var debugReceiver: DebugReceiver? = null

    private val restorables = ArrayList<Restorable>()

    override fun attachBaseContext(base: Context) {
        val vectorConfiguration = VectorConfiguration(this)
        super.attachBaseContext(vectorConfiguration.getLocalisedContext(base))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        restorables.forEach { it.onSaveInstanceState(outState) }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        restorables.forEach { it.onRestoreInstanceState(savedInstanceState) }
        super.onRestoreInstanceState(savedInstanceState)
    }

    @MainThread
    protected fun <T : Restorable> T.register(): T {
        Util.assertMainThread()
        restorables.add(this)
        return this
    }

    override fun isTaskRoot(): Boolean {
        return true
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate Activity ${javaClass.simpleName}")
        val singletonEntryPoint = singletonEntryPoint()
        val activityEntryPoint = EntryPointAccessors.fromActivity(this, ActivityEntryPoint::class.java)
        ThemeUtils.setActivityTheme(this, getOtherThemes())
        fragmentFactory = activityEntryPoint.fragmentFactory()
        supportFragmentManager.fragmentFactory = fragmentFactory
        viewModelFactory = activityEntryPoint.viewModelFactory()
        super.onCreate(savedInstanceState)
        configurationViewModel = viewModelProvider.get(ConfigurationViewModel::class.java)
        bugReporter = singletonEntryPoint.bugReporter()
        pinLocker = singletonEntryPoint.pinLocker()
        analyticsTracker = singletonEntryPoint.analyticsTracker()
        navigator = singletonEntryPoint.navigator()
        activeSessionHolder = singletonEntryPoint.activeSessionHolder()
        vectorPreferences = singletonEntryPoint.vectorPreferences()
        configurationViewModel.activityRestarter.observe(this) {
            if (!it.hasBeenHandled) {
                
                restart()
            }
        }
        pinLocker.getLiveState().observeNotNull(this) {
            if (this@VectorBaseActivity !is UnlockedActivity && it == PinLocker.State.LOCKED) {
                navigator.openPinCode(this, pinStartForActivityResult, PinMode.AUTH)
            }
        }
        sessionListener = singletonEntryPoint.sessionListener()
        sessionListener.globalErrorLiveData.observeEvent(this) {
            handleGlobalError(it)
        }

        
        if (vectorPreferences.useFlagSecure()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        doBeforeSetContentView()

        
        applyFontSize()

        views = getBinding()
        setContentView(views.root)

        this.savedInstanceState = savedInstanceState

        initUiAndData()

        val titleRes = getTitleRes()
        if (titleRes != -1) {
            supportActionBar?.let {
                it.setTitle(titleRes)
            } ?: run {
                setTitle(titleRes)
            }
        }
    }

    
    private fun applyFontSize() {
        resources.configuration.fontScale = FontScale.getFontScaleValue(this).scale

        @Suppress("DEPRECATION")
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
    }

    private fun handleGlobalError(globalError: GlobalError) {
        when (globalError) {
            is GlobalError.InvalidToken         ->
                handleInvalidToken(globalError)
            is GlobalError.ConsentNotGivenError ->
                consentNotGivenHelper.displayDialog(globalError.consentUri,
                        activeSessionHolder.getActiveSession().sessionParams.homeServerHost ?: "")
            is GlobalError.CertificateError     ->
                handleCertificateError(globalError)
            GlobalError.ExpiredAccount          -> Unit 
            is GlobalError.InitialSyncRequest   -> handleInitialSyncRequest(globalError)
        }
    }

    private fun handleInitialSyncRequest(initialSyncRequest: GlobalError.InitialSyncRequest) {
        MaterialAlertDialogBuilder(this)
                .setTitle(R.string.initial_sync_request_title)
                .setMessage(
                        getString(R.string.initial_sync_request_content, getString(
                                when (initialSyncRequest.reason) {
                                    InitialSyncRequestReason.IGNORED_USERS_LIST_CHANGE -> R.string.initial_sync_request_reason_unignored_users
                                }
                        ))
                )
                .setPositiveButton(R.string.ok) { _, _ ->
                    MainActivity.restartApp(this, MainActivityArgs(clearCache = true))
                }
                .setNegativeButton(R.string.later, null)
                .show()
    }

    private fun handleCertificateError(certificateError: GlobalError.CertificateError) {
        singletonEntryPoint()
                .unrecognizedCertificateDialog()
                .show(this,
                        certificateError.fingerprint,
                        object : UnrecognizedCertificateDialog.Callback {
                            override fun onAccept() {
                                
                            }

                            override fun onIgnore() {
                                
                            }

                            override fun onReject() {
                                
                            }
                        }
                )
    }

    protected open fun handleInvalidToken(globalError: GlobalError.InvalidToken) {
        Timber.w("Invalid token event received")
        if (mainActivityStarted) {
            return
        }

        mainActivityStarted = true

        MainActivity.restartApp(this,
                MainActivityArgs(
                        clearCredentials = !globalError.softLogout,
                        isUserLoggedOut = true,
                        isSoftLogout = globalError.softLogout
                )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy Activity ${javaClass.simpleName}")
    }

    private val pinStartForActivityResult = registerStartForActivityResult { activityResult ->
        when (activityResult.resultCode) {
            Activity.RESULT_OK -> {
                Timber.v("Pin ok, unlock app")
                pinLocker.unlock()

                
                
                
            }
            else               -> {
                if (pinLocker.getLiveState().value != PinLocker.State.UNLOCKED) {
                    
                    finishAndRemoveTask()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume Activity ${javaClass.simpleName}")
        analyticsScreenName?.let {
            analyticsTracker.screen(MobileScreen(screenName = it))
        }
        configurationViewModel.onActivityResumed()

        if (this !is BugReportActivity && vectorPreferences.useRageshake()) {
            rageShake.start()
        }
        DebugReceiver
                .getIntentFilter(this)
                .takeIf { BuildConfig.DEBUG }
                ?.let {
                    debugReceiver = DebugReceiver()
                    registerReceiver(debugReceiver, it)
                }
    }

    private val postResumeScheduledActions = mutableListOf<() -> Unit>()

    
    protected fun doOnPostResume(action: () -> Unit) {
        synchronized(postResumeScheduledActions) {
            postResumeScheduledActions.add(action)
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        synchronized(postResumeScheduledActions) {
            postResumeScheduledActions.forEach {
                tryOrNull { it.invoke() }
            }
            postResumeScheduledActions.clear()
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause Activity ${javaClass.simpleName}")

        rageShake.stop()

        debugReceiver?.let {
            unregisterReceiver(debugReceiver)
            debugReceiver = null
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus && displayInFullscreen()) {
            setFullScreen()
        }
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration?) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)

        Timber.w("onMultiWindowModeChanged. isInMultiWindowMode: $isInMultiWindowMode")
        bugReporter.inMultiWindowMode = isInMultiWindowMode
    }

    protected fun createFragment(fragmentClass: Class<out Fragment>, argsParcelable: Parcelable? = null): Fragment {
        return fragmentFactory.instantiate(classLoader, fragmentClass.name).apply {
            arguments = argsParcelable?.toMvRxBundle()
        }
    }

    

    
    @Suppress("DEPRECATION")
    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            
            window.setDecorFitsSystemWindows(false)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                window.decorView.windowInsetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                @SuppressLint("WrongConstant")
                window.decorView.windowInsetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
            }
            
            window.statusBarColor = ContextCompat.getColor(this, im.vector.lib.attachmentviewer.R.color.half_transparent_status_bar)
            
            window.navigationBarColor = ContextCompat.getColor(this, im.vector.lib.attachmentviewer.R.color.half_transparent_status_bar)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuRes = getMenuRes()

        if (menuRes != -1) {
            menuInflater.inflate(menuRes, menu)
            return true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed(true)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        onBackPressed(false)
    }

    private fun onBackPressed(fromToolbar: Boolean) {
        val handled = recursivelyDispatchOnBackPressed(supportFragmentManager, fromToolbar)
        if (!handled) {
            super.onBackPressed()
        }
    }

    private fun recursivelyDispatchOnBackPressed(fm: FragmentManager, fromToolbar: Boolean): Boolean {
        val reverseOrder = fm.fragments.filterIsInstance<VectorBaseFragment<*>>().reversed()
        for (f in reverseOrder) {
            val handledByChildFragments = recursivelyDispatchOnBackPressed(f.childFragmentManager, fromToolbar)
            if (handledByChildFragments) {
                return true
            }
            if (f is OnBackPressed && f.onBackPressed(fromToolbar)) {
                return true
            }
        }
        return false
    }

    

    
    protected fun getSavedInstanceState(): Bundle {
        return savedInstanceState!!
    }

    
    protected fun isFirstCreation() = savedInstanceState == null

    
    
    

    var waitingView: View? = null
        set(value) {
            field = value

            
            value?.isClickable = true
        }

    
    fun isWaitingViewVisible() = waitingView?.isVisible == true

    
    open fun showWaitingView(text: String? = null) {
        waitingView?.isVisible = true
        if (text != null) {
            waitingView?.findViewById<TextView>(R.id.waitingStatusText)?.setTextOrHide(text)
        }
    }

    
    open fun hideWaitingView() {
        waitingView?.isVisible = false
    }

    

    abstract fun getBinding(): VB

    open fun displayInFullscreen() = false

    open fun doBeforeSetContentView() = Unit

    open fun initUiAndData() = Unit

    
    final override fun invalidate() = Unit

    @StringRes
    open fun getTitleRes() = -1

    @MenuRes
    open fun getMenuRes() = -1

    
    open fun getOtherThemes(): ActivityOtherThemes = ActivityOtherThemes.Default

    

    fun showSnackbar(message: String) {
        getCoordinatorLayout()?.showOptimizedSnackbar(message)
    }

    fun showSnackbar(message: String, @StringRes withActionTitle: Int?, action: (() -> Unit)?) {
        val coordinatorLayout = getCoordinatorLayout()
        if (coordinatorLayout != null) {
            Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).apply {
                withActionTitle?.let {
                    setAction(withActionTitle) { action?.invoke() }
                }
            }.show()
        } else {
            if (vectorPreferences.failFast()) {
                error("No CoordinatorLayout to display this snackbar!")
            } else {
                Timber.w("No CoordinatorLayout to display this snackbar!")
            }
        }
    }

    open fun getCoordinatorLayout(): CoordinatorLayout? = null

    

    private val consentNotGivenHelper by lazy {
        ConsentNotGivenHelper(this, DialogLocker(savedInstanceState))
                .apply { restorables.add(this) }
    }

    

    fun notImplemented(message: String = "") {
        if (message.isNotBlank()) {
            toast(getString(R.string.not_implemented) + ": $message")
        } else {
            toast(getString(R.string.not_implemented))
        }
    }

    
    fun setupToolbar(toolbar: MaterialToolbar) = ToolbarConfig(this, toolbar).also {
        this.toolbar = it.setup()
    }
}
