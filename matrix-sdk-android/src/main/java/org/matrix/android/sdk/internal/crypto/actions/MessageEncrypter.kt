

package org.matrix.android.sdk.internal.crypto.actions

import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_OLM
import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.internal.crypto.MXOlmDevice
import org.matrix.android.sdk.internal.crypto.model.rest.EncryptedMessage
import org.matrix.android.sdk.internal.di.DeviceId
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.util.JsonCanonicalizer
import org.matrix.android.sdk.internal.util.convertToUTF8
import timber.log.Timber
import javax.inject.Inject

private val loggerTag = LoggerTag("MessageEncrypter", LoggerTag.CRYPTO)

internal class MessageEncrypter @Inject constructor(
        @UserId
        private val userId: String,
        @DeviceId
        private val deviceId: String?,
        private val olmDevice: MXOlmDevice) {
    
    suspend fun encryptMessage(payloadFields: Content, deviceInfos: List<CryptoDeviceInfo>): EncryptedMessage {
        val deviceInfoParticipantKey = deviceInfos.associateBy { it.identityKey()!! }

        val payloadJson = payloadFields.toMutableMap()

        payloadJson["sender"] = userId
        payloadJson["sender_device"] = deviceId!!

        
        
        
        
        
        
        
        
        payloadJson["keys"] = mapOf("ed25519" to olmDevice.deviceEd25519Key!!)

        val ciphertext = mutableMapOf<String, Any>()

        for ((deviceKey, deviceInfo) in deviceInfoParticipantKey) {
            val sessionId = olmDevice.getSessionId(deviceKey)

            if (!sessionId.isNullOrEmpty()) {
                Timber.tag(loggerTag.value).d("Using sessionid $sessionId for device $deviceKey")

                payloadJson["recipient"] = deviceInfo.userId
                payloadJson["recipient_keys"] = mapOf("ed25519" to deviceInfo.fingerprint()!!)

                val payloadString = convertToUTF8(JsonCanonicalizer.getCanonicalJson(Map::class.java, payloadJson))
                ciphertext[deviceKey] = olmDevice.encryptMessage(deviceKey, sessionId, payloadString)!!
            }
        }

        return EncryptedMessage(
                algorithm = MXCRYPTO_ALGORITHM_OLM,
                senderKey = olmDevice.deviceCurve25519Key,
                cipherText = ciphertext
        )
    }
}
