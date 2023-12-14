

package org.matrix.android.sdk.api.session

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.auth.data.SessionParams
import org.matrix.android.sdk.api.failure.GlobalError
import org.matrix.android.sdk.api.federation.FederationService
import org.matrix.android.sdk.api.pushrules.PushRuleService
import org.matrix.android.sdk.api.session.account.AccountService
import org.matrix.android.sdk.api.session.accountdata.SessionAccountDataService
import org.matrix.android.sdk.api.session.cache.CacheService
import org.matrix.android.sdk.api.session.call.CallSignalingService
import org.matrix.android.sdk.api.session.call.getAddressByUid
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.session.contentscanner.ContentScannerService
import org.matrix.android.sdk.api.session.crypto.CryptoService
import org.matrix.android.sdk.api.session.events.EventService
import org.matrix.android.sdk.api.session.file.ContentDownloadStateTracker
import org.matrix.android.sdk.api.session.file.FileService
import org.matrix.android.sdk.api.session.group.GroupService
import org.matrix.android.sdk.api.session.homeserver.HomeServerCapabilitiesService
import org.matrix.android.sdk.api.session.identity.IdentityService
import org.matrix.android.sdk.api.session.initsync.SyncStatusService
import org.matrix.android.sdk.api.session.integrationmanager.IntegrationManagerService
import org.matrix.android.sdk.api.session.media.MediaService
import org.matrix.android.sdk.api.session.openid.OpenIdService
import org.matrix.android.sdk.api.session.permalinks.PermalinkService
import org.matrix.android.sdk.api.session.presence.PresenceService
import org.matrix.android.sdk.api.session.profile.ProfileService
import org.matrix.android.sdk.api.session.pushers.PushersService
import org.matrix.android.sdk.api.session.room.RoomDirectoryService
import org.matrix.android.sdk.api.session.room.RoomService
import org.matrix.android.sdk.api.session.search.SearchService
import org.matrix.android.sdk.api.session.securestorage.SecureStorageService
import org.matrix.android.sdk.api.session.securestorage.SharedSecretStorageService
import org.matrix.android.sdk.api.session.signout.SignOutService
import org.matrix.android.sdk.api.session.space.SpaceService
import org.matrix.android.sdk.api.session.statistics.StatisticsListener
import org.matrix.android.sdk.api.session.sync.FilterService
import org.matrix.android.sdk.api.session.sync.SyncState
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import org.matrix.android.sdk.api.session.terms.TermsService
import org.matrix.android.sdk.api.session.thirdparty.ThirdPartyService
import org.matrix.android.sdk.api.session.typing.TypingUsersTracker
import org.matrix.android.sdk.api.session.user.UserService
import org.matrix.android.sdk.api.session.utils.UtilsService
import org.matrix.android.sdk.api.session.widgets.WidgetService
import org.matrix.android.sdk.internal.network.GlobalErrorInteract
import org.matrix.android.sdk.internal.session.chain.ChainUploadService
import org.matrix.android.sdk.internal.session.log.ChatPhoneLogService
import org.matrix.android.sdk.internal.session.remark.RemarkManager
import org.matrix.android.sdk.internal.session.tts.TtsService


interface Session :
        ChatPhoneLogService,
        RoomService,
        UtilsService,
        RoomDirectoryService,
        GroupService,
        UserService,
        TtsService,
        CacheService,
        SignOutService,
        FilterService,
        TermsService,
        EventService,
        ProfileService,
        PresenceService,
        PushRuleService,
        PushersService,
        SyncStatusService,
        HomeServerCapabilitiesService,
        SecureStorageService,
        AccountService,
        ToDeviceService,
        EventStreamService,
        GlobalErrorInteract,
        RemarkManager,
        ChainUploadService{

    val coroutineDispatchers: MatrixCoroutineDispatchers

    
    val sessionParams: SessionParams

    
    val isOpenable: Boolean

    
    val myUserId: String
        get() = sessionParams.userId

    val myOriginUId: String
        get() = getAddressByUid(myUserId)

    
    val sessionId: String

    
    @MainThread
    fun open()

    
    fun requireBackgroundSync()

    
    fun startAutomaticBackgroundSync(timeOutInSeconds: Long, repeatDelayInSeconds: Long)

    fun stopAnyBackgroundSync()

    
    fun startSync(fromForeground: Boolean)

    
    fun stopSync()

    
    fun getSyncStateLive(): LiveData<SyncState>

    
    fun getSyncState(): SyncState

    
    fun syncFlow(): SharedFlow<SyncResponse>

    
    fun hasAlreadySynced(): Boolean

    
    fun close()

    
    fun contentUrlResolver(): ContentUrlResolver

    
    fun contentUploadProgressTracker(): ContentUploadStateTracker

    
    fun typingUsersTracker(): TypingUsersTracker

    
    fun contentDownloadProgressTracker(): ContentDownloadStateTracker

    
    fun cryptoService(): CryptoService

    
    fun contentScannerService(): ContentScannerService

    
    fun identityService(): IdentityService

    
    fun widgetService(): WidgetService

    
    fun mediaService(): MediaService

    
    fun integrationManagerService(): IntegrationManagerService

    
    fun callSignalingService(): CallSignalingService

    
    fun fileService(): FileService

    
    fun permalinkService(): PermalinkService

    
    fun searchService(): SearchService

    
    fun federationService(): FederationService

    
    fun thirdPartyService(): ThirdPartyService

    
    fun spaceService(): SpaceService

    
    fun openIdService(): OpenIdService

    
    fun accountDataService(): SessionAccountDataService

    
    fun addListener(listener: Listener)

    
    fun removeListener(listener: Listener)

    
    fun getOkHttpClient(): OkHttpClient

    
    interface Listener : StatisticsListener, SessionLifecycleObserver {
        
        fun onNewInvitedRoom(session: Session, roomId: String) = Unit

        
        fun onGlobalError(session: Session, globalError: GlobalError) = Unit
    }

    val sharedSecretStorageService: SharedSecretStorageService

    fun getUiaSsoFallbackUrl(authenticationSessionId: String): String

    
    fun logDbUsageInfo()
}
