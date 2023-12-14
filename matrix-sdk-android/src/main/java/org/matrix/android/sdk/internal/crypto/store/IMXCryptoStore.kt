

package org.matrix.android.sdk.internal.crypto.store

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import org.matrix.android.sdk.api.session.crypto.NewSessionListener
import org.matrix.android.sdk.api.session.crypto.crosssigning.CryptoCrossSigningKey
import org.matrix.android.sdk.api.session.crypto.crosssigning.MXCrossSigningInfo
import org.matrix.android.sdk.api.session.crypto.crosssigning.PrivateKeysInfo
import org.matrix.android.sdk.api.session.crypto.keysbackup.SavedKeyBackupKeyInfo
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.DeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.GossipingRequestState
import org.matrix.android.sdk.api.session.crypto.model.IncomingRoomKeyRequest
import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap
import org.matrix.android.sdk.api.session.crypto.model.OutgoingGossipingRequestState
import org.matrix.android.sdk.api.session.crypto.model.OutgoingRoomKeyRequest
import org.matrix.android.sdk.api.session.crypto.model.RoomKeyRequestBody
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.content.RoomKeyWithHeldContent
import org.matrix.android.sdk.api.util.Optional
import org.matrix.android.sdk.internal.crypto.IncomingShareRequestCommon
import org.matrix.android.sdk.internal.crypto.OutgoingSecretRequest
import org.matrix.android.sdk.internal.crypto.model.OlmInboundGroupSessionWrapper2
import org.matrix.android.sdk.internal.crypto.model.OlmSessionWrapper
import org.matrix.android.sdk.internal.crypto.model.OutboundGroupSessionWrapper
import org.matrix.android.sdk.internal.crypto.store.db.model.KeysBackupDataEntity
import org.matrix.olm.OlmAccount
import org.matrix.olm.OlmOutboundGroupSession


internal interface IMXCryptoStore {

    
    fun getDeviceId(): String

    
    fun <T> doWithOlmAccount(block: (OlmAccount) -> T): T

    fun getOrCreateOlmAccount(): OlmAccount

    
    fun getInboundGroupSessions(): List<OlmInboundGroupSessionWrapper2>

    
    fun getGlobalBlacklistUnverifiedDevices(): Boolean

    
    fun setGlobalBlacklistUnverifiedDevices(block: Boolean)

    
    fun getRoomsListBlacklistUnverifiedDevices(): List<String>

    
    fun setRoomsListBlacklistUnverifiedDevices(roomIds: List<String>)

    
    fun getKeyBackupVersion(): String?

    
    fun setKeyBackupVersion(keyBackupVersion: String?)

    
    fun getKeysBackupData(): KeysBackupDataEntity?

    
    fun setKeysBackupData(keysBackupData: KeysBackupDataEntity?)

    
    fun getDeviceTrackingStatuses(): Map<String, Int>

    
    fun getPendingIncomingRoomKeyRequests(): List<IncomingRoomKeyRequest>

    fun getPendingIncomingGossipingRequests(): List<IncomingShareRequestCommon>

    fun storeIncomingGossipingRequest(request: IncomingShareRequestCommon, ageLocalTS: Long?)

    fun storeIncomingGossipingRequests(requests: List<IncomingShareRequestCommon>)

    
    fun hasData(): Boolean

    
    fun deleteStore()

    
    fun open()

    
    fun close()

    
    fun storeDeviceId(deviceId: String)

    
    fun saveOlmAccount()

    
    fun getUserDevice(userId: String, deviceId: String): CryptoDeviceInfo?

    
    fun deviceWithIdentityKey(identityKey: String): CryptoDeviceInfo?

    
    fun storeUserDevices(userId: String, devices: Map<String, CryptoDeviceInfo>?)

    fun storeUserCrossSigningKeys(userId: String,
                                  masterKey: CryptoCrossSigningKey?,
                                  selfSigningKey: CryptoCrossSigningKey?,
                                  userSigningKey: CryptoCrossSigningKey?)

    
    fun getUserDevices(userId: String): Map<String, CryptoDeviceInfo>?

    fun getUserDeviceList(userId: String): List<CryptoDeviceInfo>?

    fun getLiveDeviceList(userId: String): LiveData<List<CryptoDeviceInfo>>

    fun getLiveDeviceList(userIds: List<String>): LiveData<List<CryptoDeviceInfo>>

    
    fun getLiveDeviceList(): LiveData<List<CryptoDeviceInfo>>

    fun getMyDevicesInfo(): List<DeviceInfo>

    fun getLiveMyDevicesInfo(): LiveData<List<DeviceInfo>>

    fun saveMyDevicesInfo(info: List<DeviceInfo>)

    
    fun storeRoomAlgorithm(roomId: String, algorithm: String?)

    
    fun getRoomAlgorithm(roomId: String): String?

    
    fun roomWasOnceEncrypted(roomId: String): Boolean

    fun shouldEncryptForInvitedMembers(roomId: String): Boolean

    fun setShouldEncryptForInvitedMembers(roomId: String, shouldEncryptForInvitedMembers: Boolean)

    
    fun storeSession(olmSessionWrapper: OlmSessionWrapper, deviceKey: String)

    
    fun getDeviceSessionIds(deviceKey: String): List<String>?

    
    fun getDeviceSession(sessionId: String, deviceKey: String): OlmSessionWrapper?

    
    fun getLastUsedSessionId(deviceKey: String): String?

    
    fun storeInboundGroupSessions(sessions: List<OlmInboundGroupSessionWrapper2>)

    
    fun getInboundGroupSession(sessionId: String, senderKey: String): OlmInboundGroupSessionWrapper2?

    
    fun getCurrentOutboundGroupSessionForRoom(roomId: String): OutboundGroupSessionWrapper?

    
    fun storeCurrentOutboundGroupSessionForRoom(roomId: String, outboundGroupSession: OlmOutboundGroupSession?)

    
    fun removeInboundGroupSession(sessionId: String, senderKey: String)

    

    
    fun resetBackupMarkers()

    
    fun markBackupDoneForInboundGroupSessions(olmInboundGroupSessionWrappers: List<OlmInboundGroupSessionWrapper2>)

    
    fun inboundGroupSessionsToBackup(limit: Int): List<OlmInboundGroupSessionWrapper2>

    
    fun inboundGroupSessionsCount(onlyBackedUp: Boolean): Int

    
    fun saveDeviceTrackingStatuses(deviceTrackingStatuses: Map<String, Int>)

    
    fun getDeviceTrackingStatus(userId: String, defaultValue: Int): Int

    
    fun getOutgoingRoomKeyRequest(requestBody: RoomKeyRequestBody): OutgoingRoomKeyRequest?

    
    fun getOrAddOutgoingRoomKeyRequest(requestBody: RoomKeyRequestBody, recipients: Map<String, List<String>>): OutgoingRoomKeyRequest?

    fun getOrAddOutgoingSecretShareRequest(secretName: String, recipients: Map<String, List<String>>): OutgoingSecretRequest?

    fun saveGossipingEvent(event: Event) = saveGossipingEvents(listOf(event))

    fun saveGossipingEvents(events: List<Event>)

    fun updateGossipingRequestState(request: IncomingShareRequestCommon, state: GossipingRequestState) {
        updateGossipingRequestState(
                requestUserId = request.userId,
                requestDeviceId = request.deviceId,
                requestId = request.requestId,
                state = state
        )
    }

    fun updateGossipingRequestState(requestUserId: String?,
                                    requestDeviceId: String?,
                                    requestId: String?,
                                    state: GossipingRequestState)

    
    fun getIncomingRoomKeyRequest(userId: String, deviceId: String, requestId: String): IncomingRoomKeyRequest?

    fun updateOutgoingGossipingRequestState(requestId: String, state: OutgoingGossipingRequestState)

    fun addNewSessionListener(listener: NewSessionListener)

    fun removeSessionListener(listener: NewSessionListener)

    
    
    

    
    fun getMyCrossSigningInfo(): MXCrossSigningInfo?

    fun setMyCrossSigningInfo(info: MXCrossSigningInfo?)

    fun getCrossSigningInfo(userId: String): MXCrossSigningInfo?
    fun getLiveCrossSigningInfo(userId: String): LiveData<Optional<MXCrossSigningInfo>>
    fun setCrossSigningInfo(userId: String, info: MXCrossSigningInfo?)

    fun markMyMasterKeyAsLocallyTrusted(trusted: Boolean)

    fun storePrivateKeysInfo(msk: String?, usk: String?, ssk: String?)
    fun storeMSKPrivateKey(msk: String?)
    fun storeSSKPrivateKey(ssk: String?)
    fun storeUSKPrivateKey(usk: String?)

    fun getCrossSigningPrivateKeys(): PrivateKeysInfo?
    fun getLiveCrossSigningPrivateKeys(): LiveData<Optional<PrivateKeysInfo>>

    fun saveBackupRecoveryKey(recoveryKey: String?, version: String?)
    fun getKeyBackupRecoveryKeyInfo(): SavedKeyBackupKeyInfo?

    fun setUserKeysAsTrusted(userId: String, trusted: Boolean = true)
    fun setDeviceTrust(userId: String, deviceId: String, crossSignedVerified: Boolean, locallyVerified: Boolean?)

    fun clearOtherUserTrust()

    fun updateUsersTrust(check: (String) -> Boolean)

    fun addWithHeldMegolmSession(withHeldContent: RoomKeyWithHeldContent)
    fun getWithHeldMegolmSession(roomId: String, sessionId: String): RoomKeyWithHeldContent?

    fun markedSessionAsShared(roomId: String?, sessionId: String, userId: String, deviceId: String,
                              deviceIdentityKey: String, chainIndex: Int)

    
    fun getSharedSessionInfo(roomId: String?, sessionId: String, deviceInfo: CryptoDeviceInfo): SharedSessionResult
    data class SharedSessionResult(val found: Boolean, val chainIndex: Int?)

    fun getSharedWithInfo(roomId: String?, sessionId: String): MXUsersDevicesMap<Int>
    

    fun getOutgoingRoomKeyRequests(): List<OutgoingRoomKeyRequest>
    fun getOutgoingRoomKeyRequestsPaged(): LiveData<PagedList<OutgoingRoomKeyRequest>>
    fun getOutgoingSecretKeyRequests(): List<OutgoingSecretRequest>
    fun getOutgoingSecretRequest(secretName: String): OutgoingSecretRequest?
    fun getIncomingRoomKeyRequests(): List<IncomingRoomKeyRequest>
    fun getIncomingRoomKeyRequestsPaged(): LiveData<PagedList<IncomingRoomKeyRequest>>
    fun getGossipingEventsTrail(): LiveData<PagedList<Event>>
    fun getGossipingEvents(): List<Event>

    fun setDeviceKeysUploaded(uploaded: Boolean)
    fun areDeviceKeysUploaded(): Boolean
    fun tidyUpDataBase()
    fun logDbUsageInfo()
}
