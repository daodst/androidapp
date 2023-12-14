

package im.vector.app.features.home

import android.content.Context
import androidx.lifecycle.asFlow
import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.config.analyticsConfig
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.extensions.takeAs
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.features.analytics.store.AnalyticsStore
import im.vector.app.features.login.ReAuthHelper
import im.vector.app.features.session.coroutineScope
import im.vector.app.features.settings.VectorPreferences
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import im.wallet.router.wallet.IWalletPay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.UserPasswordAuth
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.api.auth.registration.nextUncompletedStage
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.pushrules.RuleIds
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap
import org.matrix.android.sdk.api.session.initsync.SyncStatusService
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import org.matrix.android.sdk.api.settings.LightweightSettingsStorage
import org.matrix.android.sdk.api.util.awaitCallback
import org.matrix.android.sdk.api.util.toMatrixItem
import org.matrix.android.sdk.flow.flow
import timber.log.Timber
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class HomeActivityViewModel @AssistedInject constructor(
        @Assisted initialState: HomeActivityViewState,
        private val activeSessionHolder: ActiveSessionHolder,
        private val reAuthHelper: ReAuthHelper,
        private val context: Context,
        private val analyticsStore: AnalyticsStore,
        private val lightweightSettingsStorage: LightweightSettingsStorage,
        private val vectorPreferences: VectorPreferences
) : VectorViewModel<HomeActivityViewState, HomeActivityViewActions, HomeActivityViewEvents>(initialState) {

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<HomeActivityViewModel, HomeActivityViewState> {
        override fun create(initialState: HomeActivityViewState): HomeActivityViewModel
    }

    companion object : MavericksViewModelFactory<HomeActivityViewModel, HomeActivityViewState> by hiltMavericksViewModelFactory()

    private var isInitialized = false
    private var checkBootstrap = true
    private var onceTrusted = false

    private fun initialize() {
        if (isInitialized) return
        isInitialized = true
        cleanupFiles()
        observeInitialSync()
        checkSessionPushIsOn()
        observeCrossSigningReset()
        observeAnalytics()
        initThreadsMigration()
    }

    private fun observeAnalytics() {
        if (analyticsConfig.isEnabled) {
            analyticsStore.didAskUserConsentFlow
                    .onEach { didAskUser ->
                        if (!didAskUser) {
                            _viewEvents.post(HomeActivityViewEvents.ShowAnalyticsOptIn)
                        }
                    }
                    .launchIn(viewModelScope)
        }
    }

    private fun cleanupFiles() {
        
        activeSessionHolder.getSafeActiveSession()?.fileService()?.clearDecryptedCache()
    }

    private fun observeCrossSigningReset() {
        val safeActiveSession = activeSessionHolder.getSafeActiveSession() ?: return

        onceTrusted = safeActiveSession
                .cryptoService()
                .crossSigningService().allPrivateKeysKnown()

        safeActiveSession
                .flow()
                .liveCrossSigningInfo(safeActiveSession.myUserId)
                .onEach {
                    val isVerified = it.getOrNull()?.isTrusted() ?: false
                    if (!isVerified && onceTrusted) {
                        
                        
                        
                        safeActiveSession.getUser(safeActiveSession.myUserId)
                                ?.toMatrixItem()
                                ?.let { user ->
                                    _viewEvents.post(HomeActivityViewEvents.OnCrossSignedInvalidated(user))
                                }
                    }
                    onceTrusted = isVerified
                }
                .launchIn(viewModelScope)
    }

    
    private fun initThreadsMigration() {
        

        when {
            
            vectorPreferences.shouldNotifyUserAboutThreads() && vectorPreferences.areThreadMessagesEnabled() -> {
                Timber.i("----> Notify users about threads")
                
                
                _viewEvents.post(HomeActivityViewEvents.NotifyUserForThreadsMigration)
                vectorPreferences.userNotifiedAboutThreads()
            }
            
            vectorPreferences.shouldNotifyUserAboutThreads() && vectorPreferences.shouldMigrateThreads()     -> {
                Timber.i("----> Migrate threads with enabled labs")
                
                
                vectorPreferences.setThreadMessagesEnabled()
                lightweightSettingsStorage.setThreadMessagesEnabled(vectorPreferences.areThreadMessagesEnabled())
                
                _viewEvents.post(HomeActivityViewEvents.MigrateThreads(checkSession = false))
            }
            
            vectorPreferences.shouldMigrateThreads() && vectorPreferences.areThreadMessagesEnabled()         -> {
                Timber.i("----> Try to migrate threads")
                _viewEvents.post(HomeActivityViewEvents.MigrateThreads(checkSession = true))
            }
        }
    }

    private fun observeInitialSync() {
        val session = activeSessionHolder.getSafeActiveSession() ?: return

        session.getSyncStatusLive()
                .asFlow()
                .onEach { status ->
                    when (status) {
                        is SyncStatusService.Status.InitialSyncProgressing -> {
                            
                            checkBootstrap = true
                        }
                        is SyncStatusService.Status.Idle                   -> {
                            if (checkBootstrap) {
                                checkBootstrap = false
                                maybeBootstrapCrossSigningAfterInitialSync()
                            }
                        }
                        else                                               -> {
                        }
                    }

                    setState {
                        copy(
                                syncStatusServiceStatus = status
                        )
                    }
                }
                .launchIn(viewModelScope)
    }

    
    private fun checkSessionPushIsOn() {
        viewModelScope.launch(Dispatchers.IO) {
            
            if (reAuthHelper.data != null) return@launch
            
            if (!vectorPreferences.areNotificationEnabledForDevice()) {
                
                val mRuleMaster = activeSessionHolder.getSafeActiveSession()
                        ?.getPushRules()
                        ?.getAllRules()
                        ?.find { it.ruleId == RuleIds.RULE_ID_DISABLE_ALL }
                if (mRuleMaster?.enabled == false) {
                    
                    
                    val knownRooms = activeSessionHolder.getSafeActiveSession()?.getRoomSummaries(roomSummaryQueryParams {
                        memberships = Membership.activeMemberships()
                    })?.size ?: 0

                    
                    if (knownRooms > 1 && !vectorPreferences.didAskUserToEnableSessionPush()) {
                        
                        delay(1500)
                        _viewEvents.post(HomeActivityViewEvents.PromptToEnableSessionPush)
                    }
                }
            }
        }
    }

    private fun maybeBootstrapCrossSigningAfterInitialSync() {
        
        activeSessionHolder.getSafeActiveSession()?.coroutineScope?.launch(Dispatchers.IO) {
            val session = activeSessionHolder.getSafeActiveSession() ?: return@launch

            tryOrNull("## MaybeBootstrapCrossSigning: Failed to download keys") {
                awaitCallback<MXUsersDevicesMap<CryptoDeviceInfo>> {
                    session.cryptoService().downloadKeys(listOf(session.myUserId), true, it)
                }
            }

            
            
            val mxCrossSigningInfo = session.cryptoService().crossSigningService().getMyCrossSigningKeys()
            if (mxCrossSigningInfo != null) {
                
                Timber.d("Cross-signing is already set up for this user, is it trusted?   ${!mxCrossSigningInfo.isTrusted()}")
                if (!mxCrossSigningInfo.isTrusted()) {
                    
                    _viewEvents.post(
                            HomeActivityViewEvents.OnNewSession(
                                    session.getUser(session.myUserId)?.toMatrixItem(),
                                    
                                    false
                            )
                    )
                }
            } else {
                
                Timber.d("Initialize cross signing...")
                try {
                    awaitCallback<Unit> {
                        session.cryptoService().crossSigningService().initializeCrossSigning(
                                object : UserInteractiveAuthInterceptor {
                                    override fun performStage(flowResponse: RegistrationFlowResponse, errCode: String?, promise: Continuation<UIABaseAuth>) {
                                        
                                        if (flowResponse.nextUncompletedStage() == LoginFlowTypes.PASSWORD &&
                                                errCode == null &&
                                                reAuthHelper.data != null) {
                                            promise.resume(
                                                    UserPasswordAuth(
                                                            session = flowResponse.session,
                                                            user = session.myUserId,
                                                            password = reAuthHelper.data
                                                    )
                                            )
                                        } else {
                                            promise.resumeWithException(Exception("Cannot silently initialize cross signing, UIA missing"))
                                        }
                                    }
                                },
                                callback = it
                        )

                        Timber.d("Initialize cross signing SUCCESS")
                    }
                } catch (failure: Throwable) {
                    Timber.e(failure, "Failed to initialize cross signing")
                }
                _viewEvents.post(HomeActivityViewEvents.CrossSigning)
            }
        }
    }

    private fun refersRoomList(action: HomeActivityViewActions.RefersRoomList) {

        val safeActiveSession = activeSessionHolder.getSafeActiveSession() ?: return

        val summaries = action.list

        
        var walletPay: IWalletPay? = null
        context.applicationContext?.takeAs<IApplication>()?.apply {
            walletPay = getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            tryOrNull {
                val deviceGroups = walletPay?.httpGetMyDeviceGroups(safeActiveSession.myOriginUId)
                deviceGroups?.data?.also { result ->
                    summaries.forEach { summaries ->

                        
                        val group = result.device.find { it.groupId == summaries.roomId }
                        safeActiveSession.getRoom(summaries.roomId)?.setRoomSummaryGroupStatus(group != null)
                        group?.groupOwnerAddr?.let {
                            safeActiveSession.getRoom(summaries.roomId)?.setRoomSummaryOwner(it)
                        }
                        group?.groupLevel?.let {
                            safeActiveSession.getRoom(summaries.roomId)?.setRoomSummaryLevel(it)
                        }
                        
                        val dvmGroup = result.be_power.find { it == summaries.roomId }
                        safeActiveSession.getRoom(summaries.roomId)?.setRoomSummaryDvmStatus(!dvmGroup.isNullOrEmpty())
                    }
                }
            }
        }

    }

    override fun handle(action: HomeActivityViewActions) {
        when (action) {
            HomeActivityViewActions.PushPromptHasBeenReviewed -> {
                vectorPreferences.setDidAskUserToEnableSessionPush()
            }
            is HomeActivityViewActions.RefersRoomList         -> {
                refersRoomList(action)
            }
            HomeActivityViewActions.ViewStarted               -> {
                initialize()
            }
        }
    }
}
