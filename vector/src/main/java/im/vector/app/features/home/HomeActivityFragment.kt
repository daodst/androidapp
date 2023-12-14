

package im.vector.app.features.home

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.AppStateHandler
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.extensions.commitTransaction
import im.vector.app.core.extensions.takeAs
import im.vector.app.core.extensions.toMvRxBundle
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.core.pushers.PushersManager
import im.vector.app.core.utils.toast
import im.vector.app.databinding.ActivityHomeBinding
import im.vector.app.features.MainActivity
import im.vector.app.features.MainActivityArgs
import im.vector.app.features.analytics.plan.MobileScreen
import im.vector.app.features.crypto.quads.SharedSecureStorageAction
import im.vector.app.features.crypto.quads.SharedSecureStorageActivity
import im.vector.app.features.crypto.quads.SharedSecureStorageViewEvent
import im.vector.app.features.crypto.recover.BootstrapActions
import im.vector.app.features.crypto.recover.BootstrapStep
import im.vector.app.features.crypto.verification.VerificationAction
import im.vector.app.features.home.room.list.RoomListViewModel
import im.vector.app.features.matrixto.MatrixToBottomSheet
import im.vector.app.features.navigation.Navigator
import im.vector.app.features.notifications.NotificationDrawerManager
import im.vector.app.features.permalink.NavigationInterceptor
import im.vector.app.features.permalink.PermalinkHandler
import im.vector.app.features.permalink.PermalinkHandler.Companion.MATRIX_TO_CUSTOM_SCHEME_URL_BASE
import im.vector.app.features.permalink.PermalinkHandler.Companion.ROOM_LINK_PREFIX
import im.vector.app.features.permalink.PermalinkHandler.Companion.USER_LINK_PREFIX
import im.vector.app.features.popup.DefaultVectorAlert
import im.vector.app.features.popup.PopupAlertManager
import im.vector.app.features.popup.VerificationVectorAlert
import im.vector.app.features.rageshake.VectorUncaughtExceptionHandler
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.settings.VectorSettingsActivity
import im.vector.app.features.spaces.invite.SpaceInviteBottomSheet
import im.vector.app.features.themes.ThemeUtils
import im.vector.app.features.usercode.UserCodeActivity
import im.vector.app.features.workers.signout.ServerBackupStatusViewModel
import im.vector.app.provide.ChatStatusProvide
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.query.ActiveSpaceFilter
import org.matrix.android.sdk.api.query.RoomCategoryFilter
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupState
import org.matrix.android.sdk.api.session.initsync.SyncStatusService
import org.matrix.android.sdk.api.session.permalinks.PermalinkService
import org.matrix.android.sdk.api.session.room.RoomSummaryQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.sync.InitialSyncStrategy
import org.matrix.android.sdk.api.session.sync.initialSyncStrategy
import org.matrix.android.sdk.api.util.MatrixItem
import timber.log.Timber
import javax.inject.Inject

fun getPassphrase(privateKey: String): String {
    return "i like ${privateKey}@#"
}

@Parcelize
data class HomeTabParams(
        
        val showTab: Boolean = true,
        
        val displayMode: RoomListDisplayMode = RoomListDisplayMode.PEOPLE
) : Parcelable


@AndroidEntryPoint
class HomeActivityFragment :
        VectorBaseFragmentHost<ActivityHomeBinding>(),
        NavigationInterceptor,
        SpaceInviteBottomSheet.InteractionListener,
        MatrixToBottomSheet.InteractionListener {

    private val homeActivityViewModel: HomeActivityViewModel by activityViewModel()

    private val serverBackupStatusViewModel: ServerBackupStatusViewModel by activityViewModel()

    @Inject lateinit var activeSessionHolder: ActiveSessionHolder
    @Inject lateinit var appStateHandler: AppStateHandler
    @Inject lateinit var vectorUncaughtExceptionHandler: VectorUncaughtExceptionHandler
    @Inject lateinit var pushManager: PushersManager
    @Inject lateinit var notificationDrawerManager: NotificationDrawerManager
    @Inject lateinit var vectorPreferences: VectorPreferences
    @Inject lateinit var popupAlertManager: PopupAlertManager
    @Inject lateinit var shortcutsHandler: ShortcutsHandler
    @Inject lateinit var permalinkHandler: PermalinkHandler
    @Inject lateinit var avatarRenderer: AvatarRenderer
    @Inject lateinit var initSyncStepFormatter: InitSyncStepFormatter

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            if (f is MatrixToBottomSheet) {
                f.interactionListener = this@HomeActivityFragment
            }
            super.onFragmentResumed(fm, f)
        }

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            if (f is MatrixToBottomSheet) {
                f.interactionListener = null
            }
            super.onFragmentPaused(fm, f)
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(inflater, container, false)
    }

    private lateinit var sharedActionViewModel: HomeSharedActionViewModel

    private val sharedSecureStorageMainViewState: SharedSecureMainStorageViewModel by activityViewModel()
    private val verificationBottomSheetViewModel: VerificationBottomSheetMainViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analyticsScreenName = MobileScreen.ScreenName.Home
        vectorBaseActivity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false)

        val arguments = arguments
        childFragmentManager.commitTransaction {
            val argUntyped = arguments?.get(Mavericks.KEY_ARG)

            val bundle = if (argUntyped is HomeTabParams) {
                arguments
            } else {
                HomeTabParams(true).toMvRxBundle()
            }
            add(R.id.homeDetailFragmentContainer, HomeDetailFragment::class.java, bundle, HomeDetailFragment::class.simpleName)
        }


        sharedActionViewModel = activityViewModelProvider.get(HomeSharedActionViewModel::class.java)

        
        val args = vectorBaseActivity.intent?.getParcelableExtra<HomeActivityArgs>(Mavericks.KEY_ARG)

        args?.privateKey?.takeIf { !it.isEmpty() }?.let {
            bootstrapVM.setPrivateKey(getPassphrase(it))
        }

        if (args?.clearNotification == true) {
            notificationDrawerManager.clearAllEvents()
        }
        if (args?.inviteNotificationRoomId != null) {
            activeSessionHolder.getSafeActiveSession()?.permalinkService()?.createPermalink(args.inviteNotificationRoomId)?.let {
                navigator.openMatrixToBottomSheet(vectorBaseActivity, it)
            }
        }

        homeActivityViewModel.observeViewEvents {
            when (it) {
                is HomeActivityViewEvents.AskPasswordToInitCrossSigning -> handleAskPasswordToInitCrossSigning(it)
                is HomeActivityViewEvents.OnNewSession                  -> handleOnNewSession(it)
                HomeActivityViewEvents.PromptToEnableSessionPush        -> handlePromptToEnablePush()
                HomeActivityViewEvents.CrossSigning                     -> handleCrossSigning()
                is HomeActivityViewEvents.OnCrossSignedInvalidated      -> handleCrossSigningInvalidated(it)
                HomeActivityViewEvents.ShowAnalyticsOptIn               -> handleShowAnalyticsOptIn()
                HomeActivityViewEvents.NotifyUserForThreadsMigration    -> handleNotifyUserForThreadsMigration()
                is HomeActivityViewEvents.MigrateThreads                -> migrateThreadsIfNeeded(it.checkSession)
            }
        }
        homeActivityViewModel.onEach { renderState(it) }

        shortcutsHandler.observeRoomsAndBuildShortcuts(lifecycleScope)

        handleIntent(vectorBaseActivity.intent)

        homeActivityViewModel.handle(HomeActivityViewActions.ViewStarted)

        sharedSecureStorageMainViewState.observeViewEvents {
            if (it is SharedSecureStorageViewEvent.FinishSuccess) {
                verificationBottomSheetViewModel.initStatus()
                verificationBottomSheetViewModel.handle(
                        VerificationAction.GotResultFromSsss(
                                it.cypherResult, SharedSecureStorageActivity.DEFAULT_RESULT_KEYSTORE_ALIAS
                        )
                )
            } else if (it is SharedSecureStorageViewEvent.Error || it is SharedSecureStorageViewEvent.InlineError || it is SharedSecureStorageViewEvent.HideModalLoading || it is SharedSecureStorageViewEvent.Dismiss) {
                
                showIndeterminatePb(false, "")
            } else if (it is SharedSecureStorageViewEvent.UpdateLoadingState) {
                
                showIndeterminatePb(true, it.waitingData.message)
            } else {
                Timber.i("=================SharedSecureMainStorageViewModel========SharedSecureStorageViewEvent=======$it=========")
            }
        }

        verificationBottomSheetViewModel.onEach { state ->
            if (state.selfVerificationMode && state.verifiedFromPrivateKeys) {
                
                showIndeterminatePb(false, "")
            }
        }

        signoutCheckViewModel.onEach { state ->
            if (state.keysBackupState == KeysBackupState.Disabled) {
                signoutCheckViewModel.refreshRemoteStateIfNeeded()
            }
            if (state.keysBackupState == KeysBackupState.ReadyToBackUp) {
                showIndeterminatePb(false, "")
            }
            Timber.i("========signoutCheckViewModel=================$state======")
        }
        
        bootstrapVM.onEach { state ->
            if (TextUtils.isEmpty(state.privateKey)) {
                return@onEach
            }
            val passphrase = state.privateKey!!

            state.initializationWaitingViewData?.message?.takeIf { !TextUtils.isEmpty(it) }?.let {
                showIndeterminatePb(true, it)
            }

            when (state.step) {
                is BootstrapStep.CheckingMigration           -> {
                    
                }
                is BootstrapStep.FirstForm                   -> {
                    
                    if (state.step.keyBackUpExist) {
                        
                        
                    } else {
                        
                        
                        bootstrapVM.handle(BootstrapActions.Start(userWantsToEnterPassphrase = true))
                    }
                }
                is BootstrapStep.SetupPassphrase             -> {
                    
                    
                    bootstrapVM.handle(BootstrapActions.GoToConfirmPassphrase(passphrase))
                }
                is BootstrapStep.ConfirmPassphrase           -> {
                    
                    if (isConfirmPassphrase) {
                        return@onEach
                    }
                    isConfirmPassphrase = true
                    bootstrapVM.handle(BootstrapActions.UpdateConfirmCandidatePassphrase(passphrase))
                    bootstrapVM.handle(BootstrapActions.DoInitialize(passphrase))
                }
                is BootstrapStep.AccountReAuth               -> {
                    
                    
                }
                is BootstrapStep.Initializing                -> {
                    
                }
                is BootstrapStep.SaveRecoveryKey             -> {
                    
                    
                    bootstrapVM.handle(BootstrapActions.Completed)
                    if (isDone) {
                        return@onEach
                    }
                    signoutCheckViewModel.initialize()
                }
                is BootstrapStep.DoneSuccess                 -> {
                }
                is BootstrapStep.GetBackupSecretForMigration -> {
                }
            }
        }
    }

    private fun handleCrossSigning() {
        doBak()
    }

    

    private val bootstrapVM: BootstrapViewModel by activityViewModel()
    private val signoutCheckViewModel: CopySignoutCheckViewModel by activityViewModel()

    var isBak = false

    private fun doBak() {
        
        if (isBak) {
            return
        }
        
        isBak = true
        withState(bootstrapVM) { state ->
            state.privateKey?.let {
                showIndeterminatePb(true, "")
                bootstrapVM.initStatus(state)
            } ?: kotlin.run {
                showIndeterminatePb(false, "")
            }
        }
    }

    fun showIndeterminatePb(show: Boolean, msg: String?) {

        val loading = vectorBaseActivity
        val showText = if (TextUtils.isEmpty(msg)) getString(R.string.initializing_key) else msg!!
        if (loading is ImLoading) {
            loading.imShowOrHideLoading(show, showText)
            return
        }
        views.waitingView.waitingHorizontalProgress.apply {
            isIndeterminate = true
            isVisible = true
        }
        views.waitingView.root.isVisible = show
        views.waitingView.waitingStatusText.apply {
            text = showText
            isVisible = true
        }
    }

    var isConfirmPassphrase = false
    var isDone = false

    private fun handleShowAnalyticsOptIn() {
        navigator.openAnalyticsOptIn(vectorBaseActivity)
    }

    
    private fun migrateThreadsIfNeeded(checkSession: Boolean) {
        if (checkSession) {
            
            val args = vectorBaseActivity.intent.getParcelableExtra<HomeActivityArgs>(Mavericks.KEY_ARG)
            if (args?.hasExistingSession == true) {
                
                Timber.i("----> Migrating threads from an existing session..")
                handleThreadsMigration()
            } else {
                
                
                Timber.i("----> No thread migration needed, we are ok")
                vectorPreferences.setShouldMigrateThreads(shouldMigrate = false)
            }
        } else {
            handleThreadsMigration()
        }
    }

    
    private fun handleThreadsMigration() {
        Timber.i("----> Threads Migration detected, clearing cache and sync...")
        vectorPreferences.setShouldMigrateThreads(shouldMigrate = false)
        MainActivity.restartApp(vectorBaseActivity, MainActivityArgs(clearCache = true))
    }

    private fun handleNotifyUserForThreadsMigration() {
        MaterialAlertDialogBuilder(vectorBaseActivity).setTitle(R.string.threads_notice_migration_title).setMessage(R.string.threads_notice_migration_message).setCancelable(
                true
        ).setPositiveButton(R.string.sas_got_it) { _, _ -> }.show()
    }

    private fun handleIntent(intent: Intent?) {
        intent?.dataString?.let { deepLink ->
            val resolvedLink = when {
                
                deepLink.startsWith(MATRIX_TO_CUSTOM_SCHEME_URL_BASE) -> {
                    when {
                        deepLink.startsWith(USER_LINK_PREFIX) -> deepLink.substring(USER_LINK_PREFIX.length)
                        deepLink.startsWith(ROOM_LINK_PREFIX) -> deepLink.substring(ROOM_LINK_PREFIX.length)
                        else                                  -> null
                    }?.let { permalinkId ->
                        activeSessionHolder.getSafeActiveSession()?.permalinkService()?.createPermalink(permalinkId)
                    }
                }
                else                                                  -> deepLink
            }

            lifecycleScope.launch {
                val isHandled = permalinkHandler.launch(
                        context = vectorBaseActivity, deepLink = resolvedLink, navigationInterceptor = this@HomeActivityFragment, buildTask = true
                )
                if (!isHandled) {
                    val isMatrixToLink = deepLink.startsWith(PermalinkService.MATRIX_TO_URL_BASE) || deepLink.startsWith(MATRIX_TO_CUSTOM_SCHEME_URL_BASE)
                    MaterialAlertDialogBuilder(vectorBaseActivity).setTitle(R.string.dialog_title_error).setMessage(if (isMatrixToLink) R.string.permalink_malformed else R.string.universal_link_malformed).setPositiveButton(
                            R.string.ok, null
                    ).show()
                }
            }
        }
    }

    private fun renderState(state: HomeActivityViewState) {
        val loading = vectorBaseActivity;
        if (loading is ImLoading) {
            when (val status = state.syncStatusServiceStatus) {
                is SyncStatusService.Status.InitialSyncProgressing -> {
                    val initSyncStepStr = initSyncStepFormatter.format(status.initSyncStep)
                    loading.renderState(true, initSyncStepStr, status.percentProgress)
                }
                else                                               -> {
                    Timber.i("==========handleOnNewSession============diss=======")
                    
                    loading.renderState(false, "", 0)
                }
            }
            return
        }


        when (val status = state.syncStatusServiceStatus) {
            is SyncStatusService.Status.InitialSyncProgressing -> {
                val initSyncStepStr = initSyncStepFormatter.format(status.initSyncStep)
                Timber.v("$initSyncStepStr ${status.percentProgress}")
                views.waitingView.root.setOnClickListener {
                    
                }
                views.waitingView.waitingHorizontalProgress.apply {
                    isIndeterminate = false
                    max = 100
                    progress = status.percentProgress
                    isVisible = true
                }
                views.waitingView.waitingStatusText.apply {
                    text = initSyncStepStr
                    isVisible = true
                }
                views.waitingView.root.isVisible = true
            }
            else                                               -> {
                
                views.waitingView.root.isVisible = false
            }
        }
    }

    private fun handleAskPasswordToInitCrossSigning(events: HomeActivityViewEvents.AskPasswordToInitCrossSigning) {
        
        promptSecurityEvent(
                events.userItem, R.string.upgrade_security, R.string.security_prompt_text
        ) {
            navigator.upgradeSessionSecurity(it, true)
        }
    }

    private fun handleCrossSigningInvalidated(event: HomeActivityViewEvents.OnCrossSignedInvalidated) {
        
    }

    
    private fun handleOnNewSession(event: HomeActivityViewEvents.OnNewSession) {
        withState(bootstrapVM, sharedSecureStorageMainViewState) { state, shareState ->
            val passphrase = state.privateKey
            if (passphrase.isNullOrEmpty()) {
                return@withState
            }
            showIndeterminatePb(true, "")
            sharedSecureStorageMainViewState.delayInit(shareState)
            sharedSecureStorageMainViewState.handle(SharedSecureStorageAction.SubmitPassphrase(passphrase))
        }
    }

    private fun handleOnNewSessionUnAuto(event: HomeActivityViewEvents.OnNewSession) {
        promptSecurityEvent(
                event.userItem, R.string.crosssigning_verify_this_session, R.string.confirm_your_identity
        ) {
            getWalletPrivateKey {
                withState(sharedSecureStorageMainViewState) { shareState ->
                    sharedSecureStorageMainViewState.delayInit(shareState)
                    sharedSecureStorageMainViewState.handle(SharedSecureStorageAction.SubmitPassphrase(it))
                }
            }
        }
    }

    private fun getWalletPrivateKey(consumer: (String) -> Unit) {
        MaterialAlertDialogBuilder(vectorBaseActivity).setTitle(getString(R.string.bottom_sheet_logout_and_backup_tips_title)).setMessage(getString(R.string.bottom_sheet_logout_and_backup_tips)).setPositiveButton(
                R.string.backup
        ) { _, _ ->
            vectorBaseActivity.applicationContext?.takeAs<IApplication>()?.apply {
                getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay?.showPayDialog(
                        vectorBaseActivity,
                        ChatStatusProvide.getAddress(vectorBaseActivity),
                ) {
                    it?.let {
                        val passphrase = getPassphrase(it)
                        consumer(passphrase)
                    } ?: kotlin.run {
                        vectorBaseActivity.toast(getString(R.string.bottom_sheet_logout_and_backup_wallet_error))
                    }
                }
            }
        }.setNegativeButton(getString(R.string.bottom_sheet_logout_and_backup_bt_diss)) { _, _ ->
        }.show()
    }

    private fun handlePromptToEnablePush() {
        popupAlertManager.postVectorAlert(DefaultVectorAlert(uid = "enablePush",
                title = getString(R.string.alert_push_are_disabled_title),
                description = getString(R.string.alert_push_are_disabled_description),
                iconId = R.drawable.ic_room_actions_notifications_mutes,
                shouldBeDisplayedIn = {
                    it is HomeActivity
                }).apply {
            colorInt = ThemeUtils.getColor(vectorBaseActivity, R.attr.vctr_notice_secondary)
            contentAction = Runnable {
                (weakCurrentActivity?.get())?.let {
                    
                    homeActivityViewModel.handle(HomeActivityViewActions.PushPromptHasBeenReviewed)
                    navigator.openSettings(it, VectorSettingsActivity.EXTRA_DIRECT_ACCESS_NOTIFICATIONS)
                }
            }
            dismissedAction = Runnable {
                homeActivityViewModel.handle(HomeActivityViewActions.PushPromptHasBeenReviewed)
            }
            addButton(getString(R.string.action_dismiss), {
                homeActivityViewModel.handle(HomeActivityViewActions.PushPromptHasBeenReviewed)
            }, true)
            addButton(getString(R.string.settings), {
                (weakCurrentActivity?.get())?.let {
                    
                    homeActivityViewModel.handle(HomeActivityViewActions.PushPromptHasBeenReviewed)
                    navigator.openSettings(it, VectorSettingsActivity.EXTRA_DIRECT_ACCESS_NOTIFICATIONS)
                }
            }, true)
        })
    }

    private fun promptSecurityEvent(userItem: MatrixItem.UserItem?, titleRes: Int, descRes: Int, action: ((AppCompatActivity) -> Unit)) {
        popupAlertManager.postVectorAlert(VerificationVectorAlert(
                uid = "upgradeSecurity", title = getString(titleRes), description = getString(descRes), iconId = R.drawable.ic_shield_warning
        ).apply {
            viewBinder = VerificationVectorAlert.ViewBinder(userItem, avatarRenderer)
            colorInt = ThemeUtils.getColor(vectorBaseActivity, R.attr.colorPrimary)
            contentAction = Runnable {
                (weakCurrentActivity?.get() as? AppCompatActivity)?.let {
                    action(it)
                }
            }
            dismissedAction = Runnable {}
        })
    }

    private fun promptSecurityEventNew(userItem: MatrixItem.UserItem?, titleRes: Int, descRes: Int, action: ((AppCompatActivity) -> Unit)) {
        popupAlertManager.postVectorAlert(VerificationVectorAlert(
                uid = "upgradeSecurity", title = getString(titleRes), description = getString(descRes), iconId = R.drawable.ic_shield_warning
        ).apply {
            viewBinder = VerificationVectorAlert.ViewBinder(userItem, avatarRenderer)
            colorInt = ThemeUtils.getColor(vectorBaseActivity, R.attr.colorPrimary)
            contentAction = Runnable {
                (weakCurrentActivity?.get() as? AppCompatActivity)?.let {
                    action(it)
                }
            }
            dismissedAction = Runnable {}
        })
    }

    fun onNewIntent(intent: Intent?) {
        val parcelableExtra = intent?.getParcelableExtra<HomeActivityArgs>(Mavericks.KEY_ARG)
        if (parcelableExtra?.clearNotification == true) {
            notificationDrawerManager.clearAllEvents()
        }
        if (parcelableExtra?.inviteNotificationRoomId != null) {
            activeSessionHolder.getSafeActiveSession()?.permalinkService()?.createPermalink(parcelableExtra.inviteNotificationRoomId)?.let {
                navigator.openMatrixToBottomSheet(requireContext(), it)
            }
        }
        handleIntent(intent)
    }

    override fun onDestroyView() {
        vectorBaseActivity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            tryOrNull {
                getAllRoom()
            }

        }
        
        serverBackupStatusViewModel.refreshRemoteStateIfNeeded()
    }

    override fun getMenuRes() = R.menu.home

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_home_setting)?.isVisible = false
        menu.findItem(R.id.menu_home_suggestion)?.isVisible = false
        menu.findItem(R.id.menu_home_report_bug)?.isVisible = false
        menu.findItem(R.id.menu_home_init_sync_legacy)?.isVisible = false
        menu.findItem(R.id.menu_home_init_sync_optimized)?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    fun fabCreateDirectChat() {
        navigator.openCreateDirectRoom(requireActivity())
    }

    fun fabOpenRoomDirectory() {
        navigator.openRoomDirectory(requireActivity(), "")
    }

    private fun launchNotification() {
        try {
            
            val intent = Intent()
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, vectorBaseActivity.getPackageName())
            intent.putExtra(Notification.EXTRA_CHANNEL_ID, vectorBaseActivity.getApplicationInfo().uid)

            
            intent.putExtra("app_package", vectorBaseActivity.getPackageName())
            intent.putExtra("app_uid", vectorBaseActivity.getApplicationInfo().uid)

            
            
            
            
            
            
            
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            
            val intent = Intent()

            
            
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", vectorBaseActivity.getPackageName(), null)
            intent.data = uri
            startActivity(intent)
        }
    }

    private fun getAllRoom() {
        val query: (RoomSummaryQueryParams.Builder) -> Unit = {
            it.memberships = listOf(Membership.JOIN)
            it.roomCategoryFilter = RoomCategoryFilter.ALL
        }
        var liveDataList: LiveData<List<RoomSummary>>? = null
        withQueryParams(query) { roomQueryParams ->
            liveDataList = activeSessionHolder.getActiveSession().getAllRoom(
                    roomQueryParams.process(RoomListViewModel.SpaceFilterStrategy.ALL_IF_SPACE_NULL, appStateHandler.safeActiveSpaceId()),
            )

        }
        liveDataList?.observe(vectorBaseActivity) {
            homeActivityViewModel.handle(HomeActivityViewActions.RefersRoomList(it))
        }
    }

    internal fun RoomSummaryQueryParams.process(spaceFilter: RoomListViewModel.SpaceFilterStrategy, currentSpace: String?): RoomSummaryQueryParams {
        return when (spaceFilter) {
            RoomListViewModel.SpaceFilterStrategy.ORPHANS_IF_SPACE_NULL -> {
                copy(
                        activeSpaceFilter = ActiveSpaceFilter.ActiveSpace(currentSpace)
                )
            }
            RoomListViewModel.SpaceFilterStrategy.ALL_IF_SPACE_NULL     -> {
                if (currentSpace == null) {
                    copy(
                            activeSpaceFilter = ActiveSpaceFilter.None
                    )
                } else {
                    copy(
                            activeSpaceFilter = ActiveSpaceFilter.ActiveSpace(currentSpace)
                    )
                }
            }
            RoomListViewModel.SpaceFilterStrategy.NONE                  -> this
        }
    }

    private fun withQueryParams(builder: (RoomSummaryQueryParams.Builder) -> Unit, block: (RoomSummaryQueryParams) -> Unit) {
        RoomSummaryQueryParams.Builder()
                .apply { builder.invoke(this) }
                .build()
                .let { block(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home_notice_setting      -> {
                launchNotification()
            }
            R.id.menu_home_qr                  -> {
                
                UserCodeActivity.newIntent(requireContext(), activeSessionHolder.getActiveSession().myUserId).let {
                    startActivity(it)
                }
            }
            R.id.menu_home_scan                -> {
                
                UserCodeActivity.newIntent(requireContext(), activeSessionHolder.getActiveSession().myUserId, 1).let {
                    startActivity(it)
                }
            }
            R.id.menu_home_create_chat_room    -> {
                fabCreateDirectChat()
            }
            R.id.menu_home_create_group_room   -> {
                fabOpenRoomDirectory()
            }
            R.id.menu_home_init_sync_legacy    -> {
                
                initialSyncStrategy = InitialSyncStrategy.Legacy
                
                MainActivity.restartApp(vectorBaseActivity, MainActivityArgs(clearCache = true))
                return true
            }
            R.id.menu_home_init_sync_optimized -> {
                
                initialSyncStrategy = InitialSyncStrategy.Optimized()
                
                MainActivity.restartApp(vectorBaseActivity, MainActivityArgs(clearCache = true))
                return true
            }
            R.id.menu_home_filter              -> {
                navigator.openRoomsFiltering(vectorBaseActivity)
                return true
            }
            R.id.menu_home_setting             -> {
                navigator.openSettings(vectorBaseActivity)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun navToMemberProfile(userId: String, deepLink: Uri): Boolean {
        
        MatrixToBottomSheet.withLink(deepLink.toString()).show(vectorBaseActivity.supportFragmentManager, "HA#MatrixToBottomSheet")
        return true
    }

    override fun navToRoom(roomId: String?, eventId: String?, deepLink: Uri?, rootThreadEventId: String?): Boolean {
        if (roomId == null) return false
        MatrixToBottomSheet.withLink(deepLink.toString()).show(vectorBaseActivity.supportFragmentManager, "HA#MatrixToBottomSheet")
        return true
    }

    override fun spaceInviteBottomSheetOnAccept(spaceId: String) {
        navigator.switchToSpace(vectorBaseActivity, spaceId, Navigator.PostSwitchSpaceAction.OpenRoomList)
    }

    override fun spaceInviteBottomSheetOnDecline(spaceId: String) {
        
    }

    companion object {

        fun getFragment(showTab: Boolean, displayMode: RoomListDisplayMode): HomeActivityFragment {
            val args = HomeTabParams(
                    showTab = showTab,
                    displayMode = displayMode
            )
            val fragment = HomeActivityFragment()
            fragment.apply {
                arguments = args.toMvRxBundle()
            }
            return fragment
        }

        fun newIntent(context: Context, clearNotification: Boolean = false, accountCreation: Boolean = false, existingSession: Boolean = false, inviteNotificationRoomId: String? = null): Intent {
            val args = HomeActivityArgs(
                    clearNotification = clearNotification,
                    accountCreation = accountCreation,
                    hasExistingSession = existingSession,
                    inviteNotificationRoomId = inviteNotificationRoomId
            )

            return Intent(context, HomeActivity::class.java).apply {
                putExtra(Mavericks.KEY_ARG, args)
            }
        }
    }

    override fun mxToBottomSheetNavigateToRoom(roomId: String) {
        navigator.openRoom(vectorBaseActivity, roomId)
    }

    override fun mxToBottomSheetSwitchToSpace(spaceId: String) {
        navigator.switchToSpace(vectorBaseActivity, spaceId, Navigator.PostSwitchSpaceAction.OpenRoomList)
    }
}
