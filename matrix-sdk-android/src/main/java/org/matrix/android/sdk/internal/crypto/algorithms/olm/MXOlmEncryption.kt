

package org.matrix.android.sdk.internal.crypto.algorithms.olm

import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.internal.crypto.DeviceListManager
import org.matrix.android.sdk.internal.crypto.MXOlmDevice
import org.matrix.android.sdk.internal.crypto.actions.EnsureOlmSessionsForUsersAction
import org.matrix.android.sdk.internal.crypto.actions.MessageEncrypter
import org.matrix.android.sdk.internal.crypto.algorithms.IMXEncrypting
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore

internal class MXOlmEncryption(
        private val roomId: String,
        private val olmDevice: MXOlmDevice,
        private val cryptoStore: IMXCryptoStore,
        private val messageEncrypter: MessageEncrypter,
        private val deviceListManager: DeviceListManager,
        private val ensureOlmSessionsForUsersAction: EnsureOlmSessionsForUsersAction) :
        IMXEncrypting {

    override suspend fun encryptEventContent(eventContent: Content, eventType: String, userIds: List<String>): Content {
        
        
        
        ensureSession(userIds)
        val deviceInfos = ArrayList<CryptoDeviceInfo>()
        for (userId in userIds) {
            val devices = cryptoStore.getUserDevices(userId)?.values.orEmpty()
            for (device in devices) {
                val key = device.identityKey()
                if (key == olmDevice.deviceCurve25519Key) {
                    
                    continue
                }
                if (device.isBlocked) {
                    
                    continue
                }
                deviceInfos.add(device)
            }
        }

        val messageMap = mapOf(
                "room_id" to roomId,
                "type" to eventType,
                "content" to eventContent
        )

        messageEncrypter.encryptMessage(messageMap, deviceInfos)
        return messageMap.toContent()
    }

    
    private suspend fun ensureSession(users: List<String>) {
        deviceListManager.downloadKeys(users, false)
        ensureOlmSessionsForUsersAction.handle(users)
    }
}
