

package org.matrix.android.sdk.internal.crypto.algorithms.megolm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap
import org.matrix.android.sdk.api.session.crypto.model.forEach
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.content.RoomKeyWithHeldContent
import org.matrix.android.sdk.api.session.events.model.content.WithHeldCode
import org.matrix.android.sdk.internal.crypto.DeviceListManager
import org.matrix.android.sdk.internal.crypto.MXOlmDevice
import org.matrix.android.sdk.internal.crypto.actions.EnsureOlmSessionsForDevicesAction
import org.matrix.android.sdk.internal.crypto.actions.MessageEncrypter
import org.matrix.android.sdk.internal.crypto.algorithms.IMXEncrypting
import org.matrix.android.sdk.internal.crypto.algorithms.IMXGroupEncryption
import org.matrix.android.sdk.internal.crypto.keysbackup.DefaultKeysBackupService
import org.matrix.android.sdk.internal.crypto.model.toDebugCount
import org.matrix.android.sdk.internal.crypto.model.toDebugString
import org.matrix.android.sdk.internal.crypto.repository.WarnOnUnknownDeviceRepository
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.crypto.tasks.SendToDeviceTask
import org.matrix.android.sdk.internal.util.JsonCanonicalizer
import org.matrix.android.sdk.internal.util.convertToUTF8
import timber.log.Timber

private val loggerTag = LoggerTag("MXMegolmEncryption", LoggerTag.CRYPTO)

internal class MXMegolmEncryption(
        
        private val roomId: String,
        private val olmDevice: MXOlmDevice,
        private val defaultKeysBackupService: DefaultKeysBackupService,
        private val cryptoStore: IMXCryptoStore,
        private val deviceListManager: DeviceListManager,
        private val ensureOlmSessionsForDevicesAction: EnsureOlmSessionsForDevicesAction,
        private val myUserId: String,
        private val myDeviceId: String,
        private val sendToDeviceTask: SendToDeviceTask,
        private val messageEncrypter: MessageEncrypter,
        private val warnOnUnknownDevicesRepository: WarnOnUnknownDeviceRepository,
        private val coroutineDispatchers: MatrixCoroutineDispatchers,
        private val cryptoCoroutineScope: CoroutineScope
) : IMXEncrypting, IMXGroupEncryption {

    
    
    
    private var outboundSession: MXOutboundSessionInfo? = null

    init {
        
        outboundSession = olmDevice.restoreOutboundGroupSessionForRoom(roomId)
    }

    
    
    
    private var sessionRotationPeriodMsgs: Int = 100
    private var sessionRotationPeriodMs: Int = 7 * 24 * 3600 * 1000

    override suspend fun encryptEventContent(eventContent: Content,
                                             eventType: String,
                                             userIds: List<String>): Content {
        val ts = System.currentTimeMillis()
        Timber.tag(loggerTag.value).v("encryptEventContent : getDevicesInRoom")
        val devices = getDevicesInRoom(userIds)
        Timber.tag(loggerTag.value).d("encrypt event in room=$roomId - devices count in room ${devices.allowedDevices.toDebugCount()}")
        Timber.tag(loggerTag.value).v("encryptEventContent ${System.currentTimeMillis() - ts}: getDevicesInRoom ${devices.allowedDevices.toDebugString()}")
        val outboundSession = ensureOutboundSession(devices.allowedDevices)

        return encryptContent(outboundSession, eventType, eventContent)
                .also {
                    notifyWithheldForSession(devices.withHeldDevices, outboundSession)
                    
                    
                    olmDevice.storeOutboundGroupSessionForRoom(roomId, outboundSession.sessionId)
                    Timber.tag(loggerTag.value).d("encrypt event in room=$roomId Finished in ${System.currentTimeMillis() - ts} millis")
                }
    }

    private fun notifyWithheldForSession(devices: MXUsersDevicesMap<WithHeldCode>, outboundSession: MXOutboundSessionInfo) {
        
        cryptoCoroutineScope.launch(coroutineDispatchers.computation) {
            mutableListOf<Pair<UserDevice, WithHeldCode>>().apply {
                devices.forEach { userId, deviceId, withheldCode ->
                    this.add(UserDevice(userId, deviceId) to withheldCode)
                }
            }.groupBy(
                    { it.second },
                    { it.first }
            ).forEach { (code, targets) ->
                notifyKeyWithHeld(targets, outboundSession.sessionId, olmDevice.deviceCurve25519Key, code)
            }
        }
    }

    override fun discardSessionKey() {
        outboundSession = null
        olmDevice.discardOutboundGroupSessionForRoom(roomId)
    }

    override suspend fun preshareKey(userIds: List<String>) {
        val ts = System.currentTimeMillis()
        Timber.tag(loggerTag.value).d("preshareKey started in $roomId ...")
        val devices = getDevicesInRoom(userIds)
        val outboundSession = ensureOutboundSession(devices.allowedDevices)

        notifyWithheldForSession(devices.withHeldDevices, outboundSession)

        Timber.tag(loggerTag.value).d("preshareKey in $roomId done in  ${System.currentTimeMillis() - ts} millis")
    }

    
    private fun prepareNewSessionInRoom(): MXOutboundSessionInfo {
        Timber.tag(loggerTag.value).v("prepareNewSessionInRoom() ")
        val sessionId = olmDevice.createOutboundGroupSessionForRoom(roomId)

        val keysClaimedMap = mapOf(
                "ed25519" to olmDevice.deviceEd25519Key!!
        )

        olmDevice.addInboundGroupSession(sessionId!!, olmDevice.getSessionKey(sessionId)!!, roomId, olmDevice.deviceCurve25519Key!!,
                emptyList(), keysClaimedMap, false)

        defaultKeysBackupService.maybeBackupKeys()

        return MXOutboundSessionInfo(sessionId, SharedWithHelper(roomId, sessionId, cryptoStore))
    }

    
    private suspend fun ensureOutboundSession(devicesInRoom: MXUsersDevicesMap<CryptoDeviceInfo>): MXOutboundSessionInfo {
        Timber.tag(loggerTag.value).v("ensureOutboundSession roomId:$roomId")
        var session = outboundSession
        if (session == null ||
                
                session.needsRotation(sessionRotationPeriodMsgs, sessionRotationPeriodMs) ||
                
                session.sharedWithTooManyDevices(devicesInRoom)) {
            Timber.tag(loggerTag.value).d("roomId:$roomId Starting new megolm session because we need to rotate.")
            session = prepareNewSessionInRoom()
            outboundSession = session
        }
        val safeSession = session
        val shareMap = HashMap<String, MutableList<CryptoDeviceInfo>>()
        val userIds = devicesInRoom.userIds
        for (userId in userIds) {
            val deviceIds = devicesInRoom.getUserDeviceIds(userId)
            for (deviceId in deviceIds!!) {
                val deviceInfo = devicesInRoom.getObject(userId, deviceId)
                if (deviceInfo != null && !cryptoStore.getSharedSessionInfo(roomId, safeSession.sessionId, deviceInfo).found) {
                    val devices = shareMap.getOrPut(userId) { ArrayList() }
                    devices.add(deviceInfo)
                }
            }
        }
        val devicesCount = shareMap.entries.fold(0) { acc, new -> acc + new.value.size }
        Timber.tag(loggerTag.value).d("roomId:$roomId found $devicesCount devices without megolm session(${session.sessionId})")
        shareKey(safeSession, shareMap)
        return safeSession
    }

    
    private suspend fun shareKey(session: MXOutboundSessionInfo,
                                 devicesByUsers: Map<String, List<CryptoDeviceInfo>>) {
        
        if (devicesByUsers.isEmpty()) {
            Timber.tag(loggerTag.value).v("shareKey() : nothing more to do")
            return
        }
        
        val subMap = HashMap<String, List<CryptoDeviceInfo>>()
        var devicesCount = 0
        for ((userId, devices) in devicesByUsers) {
            subMap[userId] = devices
            devicesCount += devices.size
            if (devicesCount > 100) {
                break
            }
        }
        Timber.tag(loggerTag.value).v("shareKey() ; sessionId<${session.sessionId}> userId ${subMap.keys}")
        shareUserDevicesKey(session, subMap)
        val remainingDevices = devicesByUsers - subMap.keys
        shareKey(session, remainingDevices)
    }

    
    private suspend fun shareUserDevicesKey(session: MXOutboundSessionInfo,
                                            devicesByUser: Map<String, List<CryptoDeviceInfo>>) {
        val sessionKey = olmDevice.getSessionKey(session.sessionId)
        val chainIndex = olmDevice.getMessageIndex(session.sessionId)

        val submap = HashMap<String, Any>()
        submap["algorithm"] = MXCRYPTO_ALGORITHM_MEGOLM
        submap["room_id"] = roomId
        submap["session_id"] = session.sessionId
        submap["session_key"] = sessionKey!!
        submap["chain_index"] = chainIndex

        val payload = HashMap<String, Any>()
        payload["type"] = EventType.ROOM_KEY
        payload["content"] = submap

        var t0 = System.currentTimeMillis()
        Timber.tag(loggerTag.value).v("shareUserDevicesKey() : starts")

        val results = ensureOlmSessionsForDevicesAction.handle(devicesByUser)
        Timber.tag(loggerTag.value).v(
                """shareUserDevicesKey(): ensureOlmSessionsForDevices succeeds after ${System.currentTimeMillis() - t0} ms"""
                        .trimMargin()
        )
        val contentMap = MXUsersDevicesMap<Any>()
        var haveTargets = false
        val userIds = results.userIds
        val noOlmToNotify = mutableListOf<UserDevice>()
        for (userId in userIds) {
            val devicesToShareWith = devicesByUser[userId]
            for ((deviceID) in devicesToShareWith!!) {
                val sessionResult = results.getObject(userId, deviceID)
                if (sessionResult?.sessionId == null) {
                    
                    

                    
                    
                    
                    Timber.tag(loggerTag.value).v("shareUserDevicesKey() : No Olm Session for $userId:$deviceID mark for withheld")
                    noOlmToNotify.add(UserDevice(userId, deviceID))
                    continue
                }
                Timber.tag(loggerTag.value).v("shareUserDevicesKey() : Add to share keys contentMap for $userId:$deviceID")
                contentMap.setObject(userId, deviceID, messageEncrypter.encryptMessage(payload, listOf(sessionResult.deviceInfo)))
                haveTargets = true
            }
        }

        
        
        
        
        
        val gossipingEventBuffer = arrayListOf<Event>()
        for ((userId, devicesToShareWith) in devicesByUser) {
            for (deviceInfo in devicesToShareWith) {
                session.sharedWithHelper.markedSessionAsShared(deviceInfo, chainIndex)
                gossipingEventBuffer.add(
                        Event(
                                type = EventType.ROOM_KEY,
                                senderId = myUserId,
                                content = submap.apply {
                                    this["session_key"] = ""
                                    
                                    this["_dest"] = "$userId|${deviceInfo.deviceId}"
                                }
                        ))
            }
        }

        cryptoStore.saveGossipingEvents(gossipingEventBuffer)

        if (haveTargets) {
            t0 = System.currentTimeMillis()
            Timber.tag(loggerTag.value).i("shareUserDevicesKey() ${session.sessionId} : has target")
            Timber.tag(loggerTag.value).d("sending to device room key for ${session.sessionId} to ${contentMap.toDebugString()}")
            val sendToDeviceParams = SendToDeviceTask.Params(EventType.ENCRYPTED, contentMap)
            try {
                withContext(coroutineDispatchers.io) {
                    sendToDeviceTask.execute(sendToDeviceParams)
                }
                Timber.tag(loggerTag.value).i("shareUserDevicesKey() : sendToDevice succeeds after ${System.currentTimeMillis() - t0} ms")
            } catch (failure: Throwable) {
                
                Timber.tag(loggerTag.value).e("shareUserDevicesKey() : Failed to share <${session.sessionId}>")
            }
        } else {
            Timber.tag(loggerTag.value).i("shareUserDevicesKey() : no need to share key")
        }

        if (noOlmToNotify.isNotEmpty()) {
            
            notifyKeyWithHeld(
                    noOlmToNotify,
                    session.sessionId,
                    olmDevice.deviceCurve25519Key,
                    WithHeldCode.NO_OLM
            )
        }
    }

    private suspend fun notifyKeyWithHeld(targets: List<UserDevice>,
                                          sessionId: String,
                                          senderKey: String?,
                                          code: WithHeldCode) {
        Timber.tag(loggerTag.value).d("notifyKeyWithHeld() :sending withheld for session:$sessionId and code $code to" +
                " ${targets.joinToString { "${it.userId}|${it.deviceId}" }}")
        val withHeldContent = RoomKeyWithHeldContent(
                roomId = roomId,
                senderKey = senderKey,
                algorithm = MXCRYPTO_ALGORITHM_MEGOLM,
                sessionId = sessionId,
                codeString = code.value
        )
        val params = SendToDeviceTask.Params(
                EventType.ROOM_KEY_WITHHELD,
                MXUsersDevicesMap<Any>().apply {
                    targets.forEach {
                        setObject(it.userId, it.deviceId, withHeldContent)
                    }
                }
        )
        try {
            withContext(coroutineDispatchers.io) {
                sendToDeviceTask.execute(params)
            }
        } catch (failure: Throwable) {
            Timber.tag(loggerTag.value)
                    .e("notifyKeyWithHeld() :$sessionId Failed to send withheld  ${targets.map { "${it.userId}|${it.deviceId}" }}")
        }
    }

    
    private fun encryptContent(session: MXOutboundSessionInfo, eventType: String, eventContent: Content): Content {
        
        val payloadJson = HashMap<String, Any>()
        payloadJson["room_id"] = roomId
        payloadJson["type"] = eventType
        payloadJson["content"] = eventContent

        

        val payloadString = convertToUTF8(JsonCanonicalizer.getCanonicalJson(Map::class.java, payloadJson))
        val ciphertext = olmDevice.encryptGroupMessage(session.sessionId, payloadString)

        val map = HashMap<String, Any>()
        map["algorithm"] = MXCRYPTO_ALGORITHM_MEGOLM
        map["sender_key"] = olmDevice.deviceCurve25519Key!!
        map["ciphertext"] = ciphertext!!
        map["session_id"] = session.sessionId

        
        
        map["device_id"] = myDeviceId
        session.useCount++
        return map
    }

    
    private suspend fun getDevicesInRoom(userIds: List<String>): DeviceInRoomInfo {
        
        
        
        
        val keys = deviceListManager.downloadKeys(userIds, false)
        val encryptToVerifiedDevicesOnly = cryptoStore.getGlobalBlacklistUnverifiedDevices() ||
                cryptoStore.getRoomsListBlacklistUnverifiedDevices().contains(roomId)

        val devicesInRoom = DeviceInRoomInfo()
        val unknownDevices = MXUsersDevicesMap<CryptoDeviceInfo>()

        for (userId in keys.userIds) {
            val deviceIds = keys.getUserDeviceIds(userId) ?: continue
            for (deviceId in deviceIds) {
                val deviceInfo = keys.getObject(userId, deviceId) ?: continue
                if (warnOnUnknownDevicesRepository.warnOnUnknownDevices() && deviceInfo.isUnknown) {
                    
                    unknownDevices.setObject(userId, deviceId, deviceInfo)
                    continue
                }
                if (deviceInfo.isBlocked) {
                    
                    devicesInRoom.withHeldDevices.setObject(userId, deviceId, WithHeldCode.BLACKLISTED)
                    continue
                }

                if (!deviceInfo.isVerified && encryptToVerifiedDevicesOnly) {
                    devicesInRoom.withHeldDevices.setObject(userId, deviceId, WithHeldCode.UNVERIFIED)
                    continue
                }

                if (deviceInfo.identityKey() == olmDevice.deviceCurve25519Key) {
                    
                    continue
                }
                devicesInRoom.allowedDevices.setObject(userId, deviceId, deviceInfo)
            }
        }
        if (unknownDevices.isEmpty) {
            return devicesInRoom
        } else {
            throw MXCryptoError.UnknownDevice(unknownDevices)
        }
    }

    override suspend fun reshareKey(groupSessionId: String,
                                    userId: String,
                                    deviceId: String,
                                    senderKey: String): Boolean {
        Timber.tag(loggerTag.value).i("process reshareKey for $groupSessionId to $userId:$deviceId")
        val deviceInfo = cryptoStore.getUserDevice(userId, deviceId) ?: return false
                .also { Timber.tag(loggerTag.value).w("reshareKey: Device not found") }

        
        val wasSessionSharedWithUser = cryptoStore.getSharedSessionInfo(roomId, groupSessionId, deviceInfo)
        if (!wasSessionSharedWithUser.found) {
            
            
            notifyKeyWithHeld(listOf(UserDevice(userId, deviceId)), groupSessionId, senderKey, WithHeldCode.UNAUTHORISED)
            Timber.tag(loggerTag.value).w("reshareKey: ERROR : Never shared megolm with this device")
            return false
        }
        
        val chainIndex = wasSessionSharedWithUser.chainIndex ?: return false
                .also {
                    Timber.tag(loggerTag.value).w("reshareKey: Null chain index")
                }

        val devicesByUser = mapOf(userId to listOf(deviceInfo))
        val usersDeviceMap = try {
            ensureOlmSessionsForDevicesAction.handle(devicesByUser)
        } catch (failure: Throwable) {
            null
        }
        val olmSessionResult = usersDeviceMap?.getObject(userId, deviceId)
        if (olmSessionResult?.sessionId == null) {
            Timber.tag(loggerTag.value).w("reshareKey: no session with this device, probably because there were no one-time keys")
            return false
        }
        Timber.tag(loggerTag.value).i(" reshareKey: $groupSessionId:$chainIndex with device $userId:$deviceId using session ${olmSessionResult.sessionId}")

        val sessionHolder = try {
            olmDevice.getInboundGroupSession(groupSessionId, senderKey, roomId)
        } catch (failure: Throwable) {
            Timber.tag(loggerTag.value).e(failure, "shareKeysWithDevice: failed to get session $groupSessionId")
            return false
        }

        val export = sessionHolder.mutex.withLock {
            sessionHolder.wrapper.exportKeys()
        } ?: return false.also {
            Timber.tag(loggerTag.value).e("shareKeysWithDevice: failed to export group session $groupSessionId")
        }

        val payloadJson = mapOf(
                "type" to EventType.FORWARDED_ROOM_KEY,
                "content" to export
        )

        val encodedPayload = messageEncrypter.encryptMessage(payloadJson, listOf(deviceInfo))
        val sendToDeviceMap = MXUsersDevicesMap<Any>()
        sendToDeviceMap.setObject(userId, deviceId, encodedPayload)
        Timber.tag(loggerTag.value).i("reshareKey() : sending session $groupSessionId to $userId:$deviceId")
        val sendToDeviceParams = SendToDeviceTask.Params(EventType.ENCRYPTED, sendToDeviceMap)
        return try {
            sendToDeviceTask.execute(sendToDeviceParams)
            Timber.tag(loggerTag.value).i("reshareKey() : successfully send <$groupSessionId> to $userId:$deviceId")
            true
        } catch (failure: Throwable) {
            Timber.tag(loggerTag.value).e(failure, "reshareKey() : fail to send <$groupSessionId> to $userId:$deviceId")
            false
        }
    }

    data class DeviceInRoomInfo(
            val allowedDevices: MXUsersDevicesMap<CryptoDeviceInfo> = MXUsersDevicesMap(),
            val withHeldDevices: MXUsersDevicesMap<WithHeldCode> = MXUsersDevicesMap()
    )

    data class UserDevice(
            val userId: String,
            val deviceId: String
    )
}
