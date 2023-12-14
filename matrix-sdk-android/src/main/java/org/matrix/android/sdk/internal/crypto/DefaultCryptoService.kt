

package org.matrix.android.sdk.internal.crypto

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.squareup.moshi.Types
import dagger.Lazy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.NoOpMatrixCallback
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_OLM
import org.matrix.android.sdk.api.crypto.MXCryptoConfig
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.crypto.CryptoService
import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import org.matrix.android.sdk.api.session.crypto.NewSessionListener
import org.matrix.android.sdk.api.session.crypto.crosssigning.DeviceTrustLevel
import org.matrix.android.sdk.api.session.crypto.crosssigning.KEYBACKUP_SECRET_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.MASTER_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.SELF_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.USER_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.keyshare.GossipingRequestListener
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.DeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.DevicesListResponse
import org.matrix.android.sdk.api.session.crypto.model.ImportRoomKeysResult
import org.matrix.android.sdk.api.session.crypto.model.IncomingRoomKeyRequest
import org.matrix.android.sdk.api.session.crypto.model.MXDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.MXEncryptEventContentResult
import org.matrix.android.sdk.api.session.crypto.model.MXEventDecryptionResult
import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap
import org.matrix.android.sdk.api.session.crypto.model.OutgoingRoomKeyRequest
import org.matrix.android.sdk.api.session.crypto.model.RoomKeyRequestBody
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.content.EncryptedEventContent
import org.matrix.android.sdk.api.session.events.model.content.RoomKeyContent
import org.matrix.android.sdk.api.session.events.model.content.RoomKeyWithHeldContent
import org.matrix.android.sdk.api.session.events.model.content.SecretSendEventContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomHistoryVisibility
import org.matrix.android.sdk.api.session.room.model.RoomHistoryVisibilityContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import org.matrix.android.sdk.internal.crypto.actions.MegolmSessionDataImporter
import org.matrix.android.sdk.internal.crypto.actions.SetDeviceVerificationAction
import org.matrix.android.sdk.internal.crypto.algorithms.IMXEncrypting
import org.matrix.android.sdk.internal.crypto.algorithms.IMXGroupEncryption
import org.matrix.android.sdk.internal.crypto.algorithms.IMXWithHeldExtension
import org.matrix.android.sdk.internal.crypto.algorithms.megolm.MXMegolmEncryptionFactory
import org.matrix.android.sdk.internal.crypto.algorithms.olm.MXOlmEncryptionFactory
import org.matrix.android.sdk.internal.crypto.crosssigning.DefaultCrossSigningService
import org.matrix.android.sdk.internal.crypto.keysbackup.DefaultKeysBackupService
import org.matrix.android.sdk.internal.crypto.model.MXKey.Companion.KEY_SIGNED_CURVE_25519_TYPE
import org.matrix.android.sdk.internal.crypto.model.toRest
import org.matrix.android.sdk.internal.crypto.repository.WarnOnUnknownDeviceRepository
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.crypto.tasks.DeleteDeviceTask
import org.matrix.android.sdk.internal.crypto.tasks.GetDeviceInfoTask
import org.matrix.android.sdk.internal.crypto.tasks.GetDevicesTask
import org.matrix.android.sdk.internal.crypto.tasks.SetDeviceNameTask
import org.matrix.android.sdk.internal.crypto.tasks.UploadKeysTask
import org.matrix.android.sdk.internal.crypto.verification.DefaultVerificationService
import org.matrix.android.sdk.internal.di.DeviceId
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.extensions.foldToCallback
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.session.StreamEventsManager
import org.matrix.android.sdk.internal.session.room.membership.LoadRoomMembersTask
import org.matrix.android.sdk.internal.task.TaskExecutor
import org.matrix.android.sdk.internal.task.TaskThread
import org.matrix.android.sdk.internal.task.configureWith
import org.matrix.android.sdk.internal.task.launchToCallback
import org.matrix.android.sdk.internal.util.JsonCanonicalizer
import org.matrix.olm.OlmManager
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.math.max



private val loggerTag = LoggerTag("DefaultCryptoService", LoggerTag.CRYPTO)

@SessionScope
internal class DefaultCryptoService @Inject constructor(
        
        private val olmManager: OlmManager,
        @UserId
        private val userId: String,
        @DeviceId
        private val deviceId: String?,
        private val myDeviceInfoHolder: Lazy<MyDeviceInfoHolder>,
        
        private val cryptoStore: IMXCryptoStore,
        
        private val roomEncryptorsStore: RoomEncryptorsStore,
        
        private val olmDevice: MXOlmDevice,
        
        private val mxCryptoConfig: MXCryptoConfig,
        
        private val deviceListManager: DeviceListManager,
        
        private val keysBackupService: DefaultKeysBackupService,
        
        private val objectSigner: ObjectSigner,
        
        private val oneTimeKeysUploader: OneTimeKeysUploader,
        
        private val roomDecryptorProvider: RoomDecryptorProvider,
        
        private val verificationService: DefaultVerificationService,

        private val crossSigningService: DefaultCrossSigningService,
        
        private val incomingGossipingRequestManager: IncomingGossipingRequestManager,
        
        private val outgoingGossipingRequestManager: OutgoingGossipingRequestManager,
        
        private val setDeviceVerificationAction: SetDeviceVerificationAction,
        private val megolmSessionDataImporter: MegolmSessionDataImporter,
        private val warnOnUnknownDevicesRepository: WarnOnUnknownDeviceRepository,
        
        private val megolmEncryptionFactory: MXMegolmEncryptionFactory,
        private val olmEncryptionFactory: MXOlmEncryptionFactory,
        
        private val deleteDeviceTask: DeleteDeviceTask,
        private val getDevicesTask: GetDevicesTask,
        private val getDeviceInfoTask: GetDeviceInfoTask,
        private val setDeviceNameTask: SetDeviceNameTask,
        private val uploadKeysTask: UploadKeysTask,
        private val loadRoomMembersTask: LoadRoomMembersTask,
        private val cryptoSessionInfoProvider: CryptoSessionInfoProvider,
        private val coroutineDispatchers: MatrixCoroutineDispatchers,
        private val taskExecutor: TaskExecutor,
        private val cryptoCoroutineScope: CoroutineScope,
        private val eventDecryptor: EventDecryptor,
        private val liveEventManager: Lazy<StreamEventsManager>
) : CryptoService {

    private val isStarting = AtomicBoolean(false)
    private val isStarted = AtomicBoolean(false)

    fun onStateEvent(roomId: String, event: Event) {
        when (event.type) {
            EventType.STATE_ROOM_ENCRYPTION         -> onRoomEncryptionEvent(roomId, event)
            EventType.STATE_ROOM_MEMBER             -> onRoomMembershipEvent(roomId, event)
            EventType.STATE_ROOM_HISTORY_VISIBILITY -> onRoomHistoryVisibilityEvent(roomId, event)
        }
    }

    fun onLiveEvent(roomId: String, event: Event) {
        
        if (event.isStateEvent()) {
            when (event.type) {
                EventType.STATE_ROOM_ENCRYPTION         -> onRoomEncryptionEvent(roomId, event)
                EventType.STATE_ROOM_MEMBER             -> onRoomMembershipEvent(roomId, event)
                EventType.STATE_ROOM_HISTORY_VISIBILITY -> onRoomHistoryVisibilityEvent(roomId, event)
            }
        }
    }

    val gossipingBuffer = mutableListOf<Event>()

    override fun setDeviceName(deviceId: String, deviceName: String, callback: MatrixCallback<Unit>) {
        setDeviceNameTask
                .configureWith(SetDeviceNameTask.Params(deviceId, deviceName)) {
                    this.executionThread = TaskThread.CRYPTO
                    this.callback = object : MatrixCallback<Unit> {
                        override fun onSuccess(data: Unit) {
                            
                            downloadKeys(listOf(userId), true, NoOpMatrixCallback())
                            callback.onSuccess(data)
                        }

                        override fun onFailure(failure: Throwable) {
                            callback.onFailure(failure)
                        }
                    }
                }
                .executeBy(taskExecutor)
    }

    override fun deleteDevice(deviceId: String, userInteractiveAuthInterceptor: UserInteractiveAuthInterceptor, callback: MatrixCallback<Unit>) {
        deleteDeviceTask
                .configureWith(DeleteDeviceTask.Params(deviceId, userInteractiveAuthInterceptor, null)) {
                    this.executionThread = TaskThread.CRYPTO
                    this.callback = callback
                }
                .executeBy(taskExecutor)
    }

    override fun getCryptoVersion(context: Context, longFormat: Boolean): String {
        return if (longFormat) olmManager.getDetailedVersion(context) else olmManager.version
    }

    override fun getMyDevice(): CryptoDeviceInfo {
        return myDeviceInfoHolder.get().myDevice
    }

    override fun fetchDevicesList(callback: MatrixCallback<DevicesListResponse>) {
        getDevicesTask
                .configureWith {
                    
                    this.callback = object : MatrixCallback<DevicesListResponse> {
                        override fun onFailure(failure: Throwable) {
                            callback.onFailure(failure)
                        }

                        override fun onSuccess(data: DevicesListResponse) {
                            
                            cryptoStore.saveMyDevicesInfo(data.devices.orEmpty())
                            callback.onSuccess(data)
                        }
                    }
                }
                .executeBy(taskExecutor)
    }

    override fun getLiveMyDevicesInfo(): LiveData<List<DeviceInfo>> {
        return cryptoStore.getLiveMyDevicesInfo()
    }

    override fun getMyDevicesInfo(): List<DeviceInfo> {
        return cryptoStore.getMyDevicesInfo()
    }

    override fun getDeviceInfo(deviceId: String, callback: MatrixCallback<DeviceInfo>) {
        getDeviceInfoTask
                .configureWith(GetDeviceInfoTask.Params(deviceId)) {
                    this.executionThread = TaskThread.CRYPTO
                    this.callback = callback
                }
                .executeBy(taskExecutor)
    }

    override fun inboundGroupSessionsCount(onlyBackedUp: Boolean): Int {
        return cryptoStore.inboundGroupSessionsCount(onlyBackedUp)
    }

    
    override fun getDeviceTrackingStatus(userId: String): Int {
        return cryptoStore.getDeviceTrackingStatus(userId, DeviceListManager.TRACKING_STATUS_NOT_TRACKED)
    }

    
    fun isStarted(): Boolean {
        return isStarted.get()
    }

    
    fun isStarting(): Boolean {
        return isStarting.get()
    }

    
    fun start() {
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            internalStart()
        }
        
        fetchDevicesList(NoOpMatrixCallback())

        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            cryptoStore.tidyUpDataBase()
        }
    }

    fun ensureDevice() {
        cryptoCoroutineScope.launchToCallback(coroutineDispatchers.crypto, NoOpMatrixCallback()) {
            
            cryptoStore.open()

            if (!cryptoStore.areDeviceKeysUploaded()) {
                
                oneTimeKeysUploader.updateOneTimeKeyCount(0)
            }

            
            tryOrNull {
                uploadDeviceKeys()
            }

            oneTimeKeysUploader.maybeUploadOneTimeKeys()
            
            tryOrNull {
                keysBackupService.checkAndStartKeysBackup()
            }
        }
    }

    fun onSyncWillProcess(isInitialSync: Boolean) {
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            if (isInitialSync) {
                try {
                    
                    
                    
                    
                    deviceListManager.invalidateAllDeviceLists()
                    
                    deviceListManager.startTrackingDeviceList(listOf(userId))
                    deviceListManager.refreshOutdatedDeviceLists()
                } catch (failure: Throwable) {
                    Timber.tag(loggerTag.value).e(failure, "onSyncWillProcess ")
                }
            }
        }
    }

    private fun internalStart() {
        if (isStarted.get() || isStarting.get()) {
            return
        }
        isStarting.set(true)

        
        cryptoStore.open()

        runCatching {

            
            incomingGossipingRequestManager.processReceivedGossipingRequests()
        }.fold(
                {
                    isStarting.set(false)
                    isStarted.set(true)
                },
                {
                    isStarting.set(false)
                    isStarted.set(false)
                    Timber.tag(loggerTag.value).e(it, "Start failed")
                }
        )
    }

    
    fun close() = runBlocking(coroutineDispatchers.crypto) {
        cryptoCoroutineScope.coroutineContext.cancelChildren(CancellationException("Closing crypto module"))
        incomingGossipingRequestManager.close()
        olmDevice.release()
        cryptoStore.close()
    }

    
    override fun isCryptoEnabled() = true

    
    override fun keysBackupService() = keysBackupService

    
    override fun verificationService() = verificationService

    override fun crossSigningService() = crossSigningService

    
    fun onSyncCompleted(syncResponse: SyncResponse) {
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            runCatching {
                if (syncResponse.deviceLists != null) {
                    deviceListManager.handleDeviceListsChanges(syncResponse.deviceLists.changed, syncResponse.deviceLists.left)
                }
                if (syncResponse.deviceOneTimeKeysCount != null) {
                    val currentCount = syncResponse.deviceOneTimeKeysCount.signedCurve25519 ?: 0
                    oneTimeKeysUploader.updateOneTimeKeyCount(currentCount)
                }

                
                try {
                    eventDecryptor.unwedgeDevicesIfNeeded()
                } catch (failure: Throwable) {
                    Timber.tag(loggerTag.value).w("unwedgeDevicesIfNeeded failed")
                }

                
                
                
                
                
                
                
                
                
                val toDevices = syncResponse.toDevice?.events.orEmpty()
                if (isStarted() && toDevices.isEmpty()) {
                    
                    deviceListManager.refreshOutdatedDeviceLists()
                    
                    
                    if (syncResponse.deviceUnusedFallbackKeyTypes != null &&
                            
                            !syncResponse.deviceUnusedFallbackKeyTypes.contains(KEY_SIGNED_CURVE_25519_TYPE)) {
                        oneTimeKeysUploader.needsNewFallback()
                    }

                    oneTimeKeysUploader.maybeUploadOneTimeKeys()
                    incomingGossipingRequestManager.processReceivedGossipingRequests()
                }
            }

            tryOrNull {
                gossipingBuffer.toList().let {
                    cryptoStore.saveGossipingEvents(it)
                }
                gossipingBuffer.clear()
            }
        }
    }

    
    override fun deviceWithIdentityKey(senderKey: String, algorithm: String): CryptoDeviceInfo? {
        return if (algorithm != MXCRYPTO_ALGORITHM_MEGOLM && algorithm != MXCRYPTO_ALGORITHM_OLM) {
            
            null
        } else cryptoStore.deviceWithIdentityKey(senderKey)
    }

    
    override fun getDeviceInfo(userId: String, deviceId: String?): CryptoDeviceInfo? {
        return if (userId.isNotEmpty() && !deviceId.isNullOrEmpty()) {
            cryptoStore.getUserDevice(userId, deviceId)
        } else {
            null
        }
    }

    override fun getCryptoDeviceInfo(userId: String): List<CryptoDeviceInfo> {
        return cryptoStore.getUserDeviceList(userId).orEmpty()
    }

    override fun getLiveCryptoDeviceInfo(): LiveData<List<CryptoDeviceInfo>> {
        return cryptoStore.getLiveDeviceList()
    }

    override fun getLiveCryptoDeviceInfo(userId: String): LiveData<List<CryptoDeviceInfo>> {
        return cryptoStore.getLiveDeviceList(userId)
    }

    override fun getLiveCryptoDeviceInfo(userIds: List<String>): LiveData<List<CryptoDeviceInfo>> {
        return cryptoStore.getLiveDeviceList(userIds)
    }

    
    override fun setDevicesKnown(devices: List<MXDeviceInfo>, callback: MatrixCallback<Unit>?) {
        
        val devicesIdListByUserId = devices.groupBy({ it.userId }, { it.deviceId })

        for ((userId, deviceIds) in devicesIdListByUserId) {
            val storedDeviceIDs = cryptoStore.getUserDevices(userId)

            
            if (null != storedDeviceIDs) {
                var isUpdated = false

                deviceIds.forEach { deviceId ->
                    val device = storedDeviceIDs[deviceId]

                    
                    
                    if (device?.isUnknown == true) {
                        device.trustLevel = DeviceTrustLevel(crossSigningVerified = false, locallyVerified = false)
                        isUpdated = true
                    }
                }

                if (isUpdated) {
                    cryptoStore.storeUserDevices(userId, storedDeviceIDs)
                }
            }
        }

        callback?.onSuccess(Unit)
    }

    
    override fun setDeviceVerification(trustLevel: DeviceTrustLevel, userId: String, deviceId: String) {
        setDeviceVerificationAction.handle(trustLevel, userId, deviceId)
    }

    
    private suspend fun setEncryptionInRoom(roomId: String,
                                            algorithm: String?,
                                            inhibitDeviceQuery: Boolean,
                                            membersId: List<String>): Boolean {
        
        
        val existingAlgorithm = cryptoStore.getRoomAlgorithm(roomId)

        if (existingAlgorithm == algorithm && roomEncryptorsStore.get(roomId) != null) {
            
            Timber.tag(loggerTag.value).e("setEncryptionInRoom() : Ignoring m.room.encryption for same alg ($algorithm) in  $roomId")
            return false
        }

        val encryptingClass = MXCryptoAlgorithms.hasEncryptorClassForAlgorithm(algorithm)

        
        cryptoStore.storeRoomAlgorithm(roomId, algorithm)

        if (!encryptingClass) {
            Timber.tag(loggerTag.value).e("setEncryptionInRoom() : Unable to encrypt room $roomId with $algorithm")
            return false
        }

        val alg: IMXEncrypting? = when (algorithm) {
            MXCRYPTO_ALGORITHM_MEGOLM -> megolmEncryptionFactory.create(roomId)
            MXCRYPTO_ALGORITHM_OLM    -> olmEncryptionFactory.create(roomId)
            else                      -> null
        }

        if (alg != null) {
            roomEncryptorsStore.put(roomId, alg)
        }

        
        
        
        
        
        if (null == existingAlgorithm) {
            Timber.tag(loggerTag.value).d("Enabling encryption in $roomId for the first time; invalidating device lists for all users therein")

            val userIds = ArrayList(membersId)

            deviceListManager.startTrackingDeviceList(userIds)

            if (!inhibitDeviceQuery) {
                deviceListManager.refreshOutdatedDeviceLists()
            }
        }

        return true
    }

    
    override fun isRoomEncrypted(roomId: String): Boolean {
        return cryptoSessionInfoProvider.isRoomEncrypted(roomId)
    }

    
    override fun getUserDevices(userId: String): MutableList<CryptoDeviceInfo> {
        return cryptoStore.getUserDevices(userId)?.values?.toMutableList() ?: ArrayList()
    }

    private fun isEncryptionEnabledForInvitedUser(): Boolean {
        return mxCryptoConfig.enableEncryptionForInvitedMembers
    }

    override fun getEncryptionAlgorithm(roomId: String): String? {
        return cryptoStore.getRoomAlgorithm(roomId)
    }

    
    override fun shouldEncryptForInvitedMembers(roomId: String): Boolean {
        return cryptoStore.shouldEncryptForInvitedMembers(roomId)
    }

    
    override fun encryptEventContent(eventContent: Content,
                                     eventType: String,
                                     roomId: String,
                                     callback: MatrixCallback<MXEncryptEventContentResult>) {
        
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            val userIds = getRoomUserIds(roomId)
            var alg = roomEncryptorsStore.get(roomId)
            if (alg == null) {
                val algorithm = getEncryptionAlgorithm(roomId)
                if (algorithm != null) {
                    if (setEncryptionInRoom(roomId, algorithm, false, userIds)) {
                        alg = roomEncryptorsStore.get(roomId)
                    }
                }
            }
            val safeAlgorithm = alg
            if (safeAlgorithm != null) {
                val t0 = System.currentTimeMillis()
                Timber.tag(loggerTag.value).v("encryptEventContent() starts")
                runCatching {
                    val content = safeAlgorithm.encryptEventContent(eventContent, eventType, userIds)
                    Timber.tag(loggerTag.value).v("## CRYPTO | encryptEventContent() : succeeds after ${System.currentTimeMillis() - t0} ms")
                    MXEncryptEventContentResult(content, EventType.ENCRYPTED)
                }.foldToCallback(callback)
            } else {
                val algorithm = getEncryptionAlgorithm(roomId)
                val reason = String.format(MXCryptoError.UNABLE_TO_ENCRYPT_REASON,
                        algorithm ?: MXCryptoError.NO_MORE_ALGORITHM_REASON)
                Timber.tag(loggerTag.value).e("encryptEventContent() : failed $reason")
                callback.onFailure(Failure.CryptoError(MXCryptoError.Base(MXCryptoError.ErrorType.UNABLE_TO_ENCRYPT, reason)))
            }
        }
    }

    override fun discardOutboundSession(roomId: String) {
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            val roomEncryptor = roomEncryptorsStore.get(roomId)
            if (roomEncryptor is IMXGroupEncryption) {
                roomEncryptor.discardSessionKey()
            } else {
                Timber.tag(loggerTag.value).e("discardOutboundSession() for:$roomId: Unable to handle IMXGroupEncryption")
            }
        }
    }

    
    @Throws(MXCryptoError::class)
    override suspend fun decryptEvent(event: Event, timeline: String): MXEventDecryptionResult {
        return internalDecryptEvent(event, timeline)
    }

    
    override fun decryptEventAsync(event: Event, timeline: String, callback: MatrixCallback<MXEventDecryptionResult>) {
        eventDecryptor.decryptEventAsync(event, timeline, callback)
    }

    
    @Throws(MXCryptoError::class)
    private suspend fun internalDecryptEvent(event: Event, timeline: String): MXEventDecryptionResult {
        return eventDecryptor.decryptEvent(event, timeline)
    }

    
    fun resetReplayAttackCheckInTimeline(timelineId: String) {
        olmDevice.resetReplayAttackCheckInTimeline(timelineId)
    }

    
    fun onToDeviceEvent(event: Event) {
        
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            when (event.getClearType()) {
                EventType.ROOM_KEY, EventType.FORWARDED_ROOM_KEY -> {
                    gossipingBuffer.add(event)
                    
                    onRoomKeyEvent(event)
                }
                EventType.REQUEST_SECRET,
                EventType.ROOM_KEY_REQUEST                       -> {
                    
                    gossipingBuffer.add(event)
                    
                    incomingGossipingRequestManager.onGossipingRequestEvent(event)
                }
                EventType.SEND_SECRET                            -> {
                    gossipingBuffer.add(event)
                    onSecretSendReceived(event)
                }
                EventType.ROOM_KEY_WITHHELD                      -> {
                    onKeyWithHeldReceived(event)
                }
                else                                             -> {
                    
                }
            }
        }
        liveEventManager.get().dispatchOnLiveToDevice(event)
    }

    
    private fun onRoomKeyEvent(event: Event) {
        val roomKeyContent = event.getClearContent().toModel<RoomKeyContent>() ?: return
        Timber.tag(loggerTag.value).i("onRoomKeyEvent() from: ${event.senderId} type<${event.getClearType()}> , sessionId<${roomKeyContent.sessionId}>")
        if (roomKeyContent.roomId.isNullOrEmpty() || roomKeyContent.algorithm.isNullOrEmpty()) {
            Timber.tag(loggerTag.value).e("onRoomKeyEvent() : missing fields")
            return
        }
        val alg = roomDecryptorProvider.getOrCreateRoomDecryptor(roomKeyContent.roomId, roomKeyContent.algorithm)
        if (alg == null) {
            Timber.tag(loggerTag.value).e("GOSSIP onRoomKeyEvent() : Unable to handle keys for ${roomKeyContent.algorithm}")
            return
        }
        alg.onRoomKeyEvent(event, keysBackupService)
    }

    private fun onKeyWithHeldReceived(event: Event) {
        val withHeldContent = event.getClearContent().toModel<RoomKeyWithHeldContent>() ?: return Unit.also {
            Timber.tag(loggerTag.value).i("Malformed onKeyWithHeldReceived() : missing fields")
        }
        Timber.tag(loggerTag.value).i("onKeyWithHeldReceived() received from:${event.senderId}, content <$withHeldContent>")
        val alg = roomDecryptorProvider.getOrCreateRoomDecryptor(withHeldContent.roomId, withHeldContent.algorithm)
        if (alg is IMXWithHeldExtension) {
            alg.onRoomKeyWithHeldEvent(withHeldContent)
        } else {
            Timber.tag(loggerTag.value).e("onKeyWithHeldReceived() from:${event.senderId}: Unable to handle WithHeldContent for ${withHeldContent.algorithm}")
            return
        }
    }

    private fun onSecretSendReceived(event: Event) {
        Timber.tag(loggerTag.value).i("GOSSIP onSecretSend() from ${event.senderId} : onSecretSendReceived ${event.content?.get("sender_key")}")
        if (!event.isEncrypted()) {
            
            Timber.tag(loggerTag.value).e("GOSSIP onSecretSend() :Received unencrypted secret send event")
            return
        }

        
        if (event.senderId != userId) {
            Timber.tag(loggerTag.value).e("GOSSIP onSecretSend() : Ignore secret from other user ${event.senderId}")
            return
        }

        val secretContent = event.getClearContent().toModel<SecretSendEventContent>() ?: return

        val existingRequest = cryptoStore
                .getOutgoingSecretKeyRequests().firstOrNull { it.requestId == secretContent.requestId }

        if (existingRequest == null) {
            Timber.tag(loggerTag.value).i("GOSSIP onSecretSend() : Ignore secret that was not requested: ${secretContent.requestId}")
            return
        }

        if (!handleSDKLevelGossip(existingRequest.secretName, secretContent.secretValue)) {
            
            Timber.tag(loggerTag.value).v("onSecretSend() : secret not handled by SDK")
        }
    }

    
    private fun handleSDKLevelGossip(secretName: String?, secretValue: String): Boolean {
        return when (secretName) {
            MASTER_KEY_SSSS_NAME       -> {
                crossSigningService.onSecretMSKGossip(secretValue)
                true
            }
            SELF_SIGNING_KEY_SSSS_NAME -> {
                crossSigningService.onSecretSSKGossip(secretValue)
                true
            }
            USER_SIGNING_KEY_SSSS_NAME -> {
                crossSigningService.onSecretUSKGossip(secretValue)
                true
            }
            KEYBACKUP_SECRET_SSSS_NAME -> {
                keysBackupService.onSecretKeyGossip(secretValue)
                true
            }
            else                       -> false
        }
    }

    
    private fun onRoomEncryptionEvent(roomId: String, event: Event) {
        if (!event.isStateEvent()) {
            
            Timber.tag(loggerTag.value).w("Invalid encryption event")
            return
        }
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            val userIds = getRoomUserIds(roomId)
            setEncryptionInRoom(roomId, event.content?.get("algorithm")?.toString(), true, userIds)
        }
    }

    private fun getRoomUserIds(roomId: String): List<String> {
        val encryptForInvitedMembers = isEncryptionEnabledForInvitedUser() &&
                shouldEncryptForInvitedMembers(roomId)
        return cryptoSessionInfoProvider.getRoomUserIds(roomId, encryptForInvitedMembers)
    }

    
    private fun onRoomMembershipEvent(roomId: String, event: Event) {
        roomEncryptorsStore.get(roomId) ?:  return

        event.stateKey?.let { userId ->
            val roomMember: RoomMemberContent? = event.content.toModel()
            val membership = roomMember?.membership
            if (membership == Membership.JOIN) {
                
                deviceListManager.startTrackingDeviceList(listOf(userId))
            } else if (membership == Membership.INVITE &&
                    shouldEncryptForInvitedMembers(roomId) &&
                    isEncryptionEnabledForInvitedUser()) {
                
                
                
                
                
                deviceListManager.startTrackingDeviceList(listOf(userId))
            }
        }
    }

    private fun onRoomHistoryVisibilityEvent(roomId: String, event: Event) {
        if (!event.isStateEvent()) return
        val eventContent = event.content.toModel<RoomHistoryVisibilityContent>()
        eventContent?.historyVisibility?.let {
            cryptoStore.setShouldEncryptForInvitedMembers(roomId, it != RoomHistoryVisibility.JOINED)
        }
    }

    
    private suspend fun uploadDeviceKeys() {
        if (cryptoStore.areDeviceKeysUploaded()) {
            Timber.tag(loggerTag.value).d("Keys already uploaded, nothing to do")
            return
        }
        
        
        val canonicalJson = JsonCanonicalizer.getCanonicalJson(Map::class.java, getMyDevice().signalableJSONDictionary())
        var rest = getMyDevice().toRest()

        rest = rest.copy(
                signatures = objectSigner.signObject(canonicalJson)
        )

        val uploadDeviceKeysParams = UploadKeysTask.Params(rest, null, null)
        uploadKeysTask.execute(uploadDeviceKeysParams)

        cryptoStore.setDeviceKeysUploaded(true)
    }

    
    override suspend fun exportRoomKeys(password: String): ByteArray {
        return exportRoomKeys(password, MXMegolmExportEncryption.DEFAULT_ITERATION_COUNT)
    }

    
    private suspend fun exportRoomKeys(password: String, anIterationCount: Int): ByteArray {
        return withContext(coroutineDispatchers.crypto) {
            val iterationCount = max(0, anIterationCount)

            val exportedSessions = cryptoStore.getInboundGroupSessions().mapNotNull { it.exportKeys() }

            val adapter = MoshiProvider.providesMoshi()
                    .adapter(List::class.java)

            MXMegolmExportEncryption.encryptMegolmKeyFile(adapter.toJson(exportedSessions), password, iterationCount)
        }
    }

    
    override suspend fun importRoomKeys(roomKeysAsArray: ByteArray,
                                        password: String,
                                        progressListener: ProgressListener?): ImportRoomKeysResult {
        return withContext(coroutineDispatchers.crypto) {
            Timber.tag(loggerTag.value).v("importRoomKeys starts")

            val t0 = System.currentTimeMillis()
            val roomKeys = MXMegolmExportEncryption.decryptMegolmKeyFile(roomKeysAsArray, password)
            val t1 = System.currentTimeMillis()

            Timber.tag(loggerTag.value).v("importRoomKeys : decryptMegolmKeyFile done in ${t1 - t0} ms")

            val importedSessions = MoshiProvider.providesMoshi()
                    .adapter<List<MegolmSessionData>>(Types.newParameterizedType(List::class.java, MegolmSessionData::class.java))
                    .fromJson(roomKeys)

            val t2 = System.currentTimeMillis()

            Timber.tag(loggerTag.value).v("importRoomKeys : JSON parsing ${t2 - t1} ms")

            if (importedSessions == null) {
                throw Exception("Error")
            }

            megolmSessionDataImporter.handle(
                    megolmSessionsData = importedSessions,
                    fromBackup = false,
                    progressListener = progressListener
            )
        }
    }

    
    override fun setWarnOnUnknownDevices(warn: Boolean) {
        warnOnUnknownDevicesRepository.setWarnOnUnknownDevices(warn)
    }

    
    fun checkUnknownDevices(userIds: List<String>, callback: MatrixCallback<Unit>) {
        
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            runCatching {
                val keys = deviceListManager.downloadKeys(userIds, true)
                val unknownDevices = getUnknownDevices(keys)
                if (unknownDevices.map.isNotEmpty()) {
                    
                    throw Failure.CryptoError(MXCryptoError.UnknownDevice(unknownDevices))
                }
            }.foldToCallback(callback)
        }
    }

    
    override fun setGlobalBlacklistUnverifiedDevices(block: Boolean) {
        cryptoStore.setGlobalBlacklistUnverifiedDevices(block)
    }

    
    override fun getGlobalBlacklistUnverifiedDevices(): Boolean {
        return cryptoStore.getGlobalBlacklistUnverifiedDevices()
    }

    
    override fun isRoomBlacklistUnverifiedDevices(roomId: String?): Boolean {
        return roomId?.let { cryptoStore.getRoomsListBlacklistUnverifiedDevices().contains(it) }
                ?: false
    }

    
    private fun setRoomBlacklistUnverifiedDevices(roomId: String, add: Boolean) {
        val roomIds = cryptoStore.getRoomsListBlacklistUnverifiedDevices().toMutableList()

        if (add) {
            if (roomId !in roomIds) {
                roomIds.add(roomId)
            }
        } else {
            roomIds.remove(roomId)
        }

        cryptoStore.setRoomsListBlacklistUnverifiedDevices(roomIds)
    }

    
    override fun setRoomBlacklistUnverifiedDevices(roomId: String) {
        setRoomBlacklistUnverifiedDevices(roomId, true)
    }

    
    override fun setRoomUnBlacklistUnverifiedDevices(roomId: String) {
        setRoomBlacklistUnverifiedDevices(roomId, false)
    }

    
    override fun cancelRoomKeyRequest(requestBody: RoomKeyRequestBody) {
        outgoingGossipingRequestManager.cancelRoomKeyRequest(requestBody)
    }

    
    override fun reRequestRoomKeyForEvent(event: Event) {
        val wireContent = event.content.toModel<EncryptedEventContent>() ?: return Unit.also {
            Timber.tag(loggerTag.value).e("reRequestRoomKeyForEvent Failed to re-request key, null content")
        }

        val requestBody = RoomKeyRequestBody(
                algorithm = wireContent.algorithm,
                roomId = event.roomId,
                senderKey = wireContent.senderKey,
                sessionId = wireContent.sessionId
        )

        outgoingGossipingRequestManager.resendRoomKeyRequest(requestBody)
    }

    override fun requestRoomKeyForEvent(event: Event) {
        val wireContent = event.content.toModel<EncryptedEventContent>() ?: return Unit.also {
            Timber.tag(loggerTag.value).e("requestRoomKeyForEvent Failed to request key, null content eventId: ${event.eventId}")
        }

        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            roomDecryptorProvider
                    .getOrCreateRoomDecryptor(event.roomId, wireContent.algorithm)
                    ?.requestKeysForEvent(event, false) ?: run {
                Timber.tag(loggerTag.value).v("requestRoomKeyForEvent() : No room decryptor for roomId:${event.roomId} algorithm:${wireContent.algorithm}")
            }
        }
    }

    
    override fun addRoomKeysRequestListener(listener: GossipingRequestListener) {
        incomingGossipingRequestManager.addRoomKeysRequestListener(listener)
    }

    
    override fun removeRoomKeysRequestListener(listener: GossipingRequestListener) {
        incomingGossipingRequestManager.removeRoomKeysRequestListener(listener)
    }







    
    private fun getUnknownDevices(devicesInRoom: MXUsersDevicesMap<CryptoDeviceInfo>): MXUsersDevicesMap<CryptoDeviceInfo> {
        val unknownDevices = MXUsersDevicesMap<CryptoDeviceInfo>()
        val userIds = devicesInRoom.userIds
        for (userId in userIds) {
            devicesInRoom.getUserDeviceIds(userId)?.forEach { deviceId ->
                devicesInRoom.getObject(userId, deviceId)
                        ?.takeIf { it.isUnknown }
                        ?.let {
                            unknownDevices.setObject(userId, deviceId, it)
                        }
            }
        }

        return unknownDevices
    }

    override fun downloadKeys(userIds: List<String>, forceDownload: Boolean, callback: MatrixCallback<MXUsersDevicesMap<CryptoDeviceInfo>>) {
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            runCatching {
                deviceListManager.downloadKeys(userIds, forceDownload)
            }.foldToCallback(callback)
        }
    }

    override fun addNewSessionListener(newSessionListener: NewSessionListener) {
        roomDecryptorProvider.addNewSessionListener(newSessionListener)
    }

    override fun removeSessionListener(listener: NewSessionListener) {
        roomDecryptorProvider.removeSessionListener(listener)
    }


    override fun toString(): String {
        return "DefaultCryptoService of $userId ($deviceId)"
    }

    override fun getOutgoingRoomKeyRequests(): List<OutgoingRoomKeyRequest> {
        return cryptoStore.getOutgoingRoomKeyRequests()
    }

    override fun getOutgoingRoomKeyRequestsPaged(): LiveData<PagedList<OutgoingRoomKeyRequest>> {
        return cryptoStore.getOutgoingRoomKeyRequestsPaged()
    }

    override fun getIncomingRoomKeyRequestsPaged(): LiveData<PagedList<IncomingRoomKeyRequest>> {
        return cryptoStore.getIncomingRoomKeyRequestsPaged()
    }

    override fun getIncomingRoomKeyRequests(): List<IncomingRoomKeyRequest> {
        return cryptoStore.getIncomingRoomKeyRequests()
    }

    override fun getGossipingEventsTrail(): LiveData<PagedList<Event>> {
        return cryptoStore.getGossipingEventsTrail()
    }

    override fun getGossipingEvents(): List<Event> {
        return cryptoStore.getGossipingEvents()
    }

    override fun getSharedWithInfo(roomId: String?, sessionId: String): MXUsersDevicesMap<Int> {
        return cryptoStore.getSharedWithInfo(roomId, sessionId)
    }

    override fun getWithHeldMegolmSession(roomId: String, sessionId: String): RoomKeyWithHeldContent? {
        return cryptoStore.getWithHeldMegolmSession(roomId, sessionId)
    }

    override fun logDbUsageInfo() {
        cryptoStore.logDbUsageInfo()
    }

    override fun prepareToEncrypt(roomId: String, callback: MatrixCallback<Unit>) {
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            Timber.tag(loggerTag.value).d("prepareToEncrypt() roomId:$roomId Check room members up to date")
            
            try {
                loadRoomMembersTask.execute(LoadRoomMembersTask.Params(roomId))
            } catch (failure: Throwable) {
                Timber.tag(loggerTag.value).e("prepareToEncrypt() : Failed to load room members")
                callback.onFailure(failure)
                return@launch
            }

            val userIds = getRoomUserIds(roomId)
            val alg = roomEncryptorsStore.get(roomId)
                    ?: getEncryptionAlgorithm(roomId)
                            ?.let { setEncryptionInRoom(roomId, it, false, userIds) }
                            ?.let { roomEncryptorsStore.get(roomId) }

            if (alg == null) {
                val reason = String.format(MXCryptoError.UNABLE_TO_ENCRYPT_REASON, MXCryptoError.NO_MORE_ALGORITHM_REASON)
                Timber.tag(loggerTag.value).e("prepareToEncrypt() : $reason")
                callback.onFailure(IllegalArgumentException("Missing algorithm"))
                return@launch
            }

            runCatching {
                (alg as? IMXGroupEncryption)?.preshareKey(userIds)
            }.fold(
                    { callback.onSuccess(Unit) },
                    {
                        Timber.tag(loggerTag.value).e(it, "prepareToEncrypt() failed.")
                        callback.onFailure(it)
                    }
            )
        }
    }

    

    @VisibleForTesting
    val cryptoStoreForTesting = cryptoStore

    @VisibleForTesting
    val olmDeviceForTest = olmDevice

    companion object {
        const val CRYPTO_MIN_FORCE_SESSION_PERIOD_MILLIS = 3_600_000 
    }
}
