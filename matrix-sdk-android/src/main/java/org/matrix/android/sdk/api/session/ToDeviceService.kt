

package org.matrix.android.sdk.api.session

import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap
import org.matrix.android.sdk.api.session.events.model.Content
import java.util.UUID

interface ToDeviceService {

    
    suspend fun sendToDevice(eventType: String, contentMap: MXUsersDevicesMap<Any>, txnId: String? = UUID.randomUUID().toString())

    suspend fun sendToDevice(eventType: String, userId: String, deviceId: String, content: Content, txnId: String? = UUID.randomUUID().toString()) {
        sendToDevice(eventType, mapOf(userId to listOf(deviceId)), content, txnId)
    }

    suspend fun sendToDevice(eventType: String, targets: Map<String, List<String>>, content: Content, txnId: String? = UUID.randomUUID().toString())

    suspend fun sendEncryptedToDevice(eventType: String, targets: Map<String, List<String>>, content: Content, txnId: String? = UUID.randomUUID().toString())
}
