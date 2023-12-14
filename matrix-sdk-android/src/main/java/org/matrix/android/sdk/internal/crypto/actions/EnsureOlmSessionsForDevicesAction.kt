

package org.matrix.android.sdk.internal.crypto.actions

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap
import org.matrix.android.sdk.internal.crypto.MXOlmDevice
import org.matrix.android.sdk.internal.crypto.model.MXKey
import org.matrix.android.sdk.internal.crypto.model.MXOlmSessionResult
import org.matrix.android.sdk.internal.crypto.tasks.ClaimOneTimeKeysForUsersDeviceTask
import org.matrix.android.sdk.internal.session.SessionScope
import timber.log.Timber
import javax.inject.Inject

private const val ONE_TIME_KEYS_RETRY_COUNT = 3

private val loggerTag = LoggerTag("EnsureOlmSessionsForDevicesAction", LoggerTag.CRYPTO)

@SessionScope
internal class EnsureOlmSessionsForDevicesAction @Inject constructor(
        private val olmDevice: MXOlmDevice,
        private val coroutineDispatchers: MatrixCoroutineDispatchers,
        private val oneTimeKeysForUsersDeviceTask: ClaimOneTimeKeysForUsersDeviceTask) {

    private val ensureMutex = Mutex()

    
    suspend fun handle(devicesByUser: Map<String, List<CryptoDeviceInfo>>, force: Boolean = false): MXUsersDevicesMap<MXOlmSessionResult> {
        ensureMutex.withLock {
            val results = MXUsersDevicesMap<MXOlmSessionResult>()
            val deviceList = devicesByUser.flatMap { it.value }
            Timber.tag(loggerTag.value)
                    .d("ensure olm forced:$force for ${deviceList.joinToString { it.shortDebugString() }}")
            val devicesToCreateSessionWith = mutableListOf<CryptoDeviceInfo>()
            if (force) {
                
                devicesToCreateSessionWith.addAll(deviceList)
            } else {
                
                deviceList.forEach { deviceInfo ->
                    val deviceId = deviceInfo.deviceId
                    val userId = deviceInfo.userId
                    val key = deviceInfo.identityKey() ?: return@forEach Unit.also {
                        Timber.tag(loggerTag.value).w("Ignoring device ${deviceInfo.shortDebugString()} without identity key")
                    }

                    
                    val sessionId = olmDevice.getSessionId(key)
                    if (sessionId.isNullOrEmpty()) {
                        Timber.tag(loggerTag.value).d("Found no existing olm session ${deviceInfo.shortDebugString()} add to claim list")
                        devicesToCreateSessionWith.add(deviceInfo)
                    } else {
                        Timber.tag(loggerTag.value).d("using olm session $sessionId for (${deviceInfo.userId}|$deviceId)")
                        val olmSessionResult = MXOlmSessionResult(deviceInfo, sessionId)
                        results.setObject(userId, deviceId, olmSessionResult)
                    }
                }
            }

            if (devicesToCreateSessionWith.isEmpty()) {
                
                return results
            }
            val usersDevicesToClaim = MXUsersDevicesMap<String>().apply {
                devicesToCreateSessionWith.forEach {
                    setObject(it.userId, it.deviceId, MXKey.KEY_SIGNED_CURVE_25519_TYPE)
                }
            }

            
            val claimParams = ClaimOneTimeKeysForUsersDeviceTask.Params(usersDevicesToClaim)
            val oneTimeKeys = withContext(coroutineDispatchers.io) {
                oneTimeKeysForUsersDeviceTask.executeRetry(claimParams, ONE_TIME_KEYS_RETRY_COUNT)
            }

            
            devicesToCreateSessionWith.forEach { deviceInfo ->
                val userId = deviceInfo.userId
                val deviceId = deviceInfo.deviceId
                
                val oneTimeKey = oneTimeKeys.getObject(userId, deviceId)
                if (oneTimeKey == null) {
                    Timber.tag(loggerTag.value).d("No otk for ${deviceInfo.shortDebugString()}")
                } else if (oneTimeKey.type != MXKey.KEY_SIGNED_CURVE_25519_TYPE) {
                    Timber.tag(loggerTag.value).d("Bad otk type (${oneTimeKey.type}) for ${deviceInfo.shortDebugString()}")
                } else {
                    val olmSessionId = verifyKeyAndStartSession(oneTimeKey, userId, deviceInfo)
                    if (olmSessionId != null) {
                        val olmSessionResult = MXOlmSessionResult(deviceInfo, olmSessionId)
                        results.setObject(userId, deviceId, olmSessionResult)
                    } else {
                        Timber
                                .tag(loggerTag.value)
                                .d("## CRYPTO | cant unwedge failed to create outbound ${deviceInfo.shortDebugString()}")
                    }
                }
            }
            return results
        }
    }

    private fun verifyKeyAndStartSession(oneTimeKey: MXKey, userId: String, deviceInfo: CryptoDeviceInfo): String? {
        var sessionId: String? = null

        val deviceId = deviceInfo.deviceId
        val signKeyId = "ed25519:$deviceId"
        val signature = oneTimeKey.signatureForUserId(userId, signKeyId)

        val fingerprint = deviceInfo.fingerprint()
        if (!signature.isNullOrEmpty() && !fingerprint.isNullOrEmpty()) {
            var isVerified = false
            var errorMessage: String? = null

            try {
                olmDevice.verifySignature(fingerprint, oneTimeKey.signalableJSONDictionary(), signature)
                isVerified = true
            } catch (e: Exception) {
                Timber.tag(loggerTag.value).d(e, "verifyKeyAndStartSession() : Verify error for otk: ${oneTimeKey.signalableJSONDictionary()}," +
                        " signature:$signature fingerprint:$fingerprint")
                Timber.tag(loggerTag.value).e("verifyKeyAndStartSession() : Verify error for ${deviceInfo.userId}|${deviceInfo.deviceId} " +
                        " - signable json ${oneTimeKey.signalableJSONDictionary()}")
                errorMessage = e.message
            }

            
            if (isVerified) {
                sessionId = deviceInfo.identityKey()?.let { identityKey ->
                    olmDevice.createOutboundSession(identityKey, oneTimeKey.value)
                }

                if (sessionId.isNullOrEmpty()) {
                    
                    Timber.tag(loggerTag.value).e("verifyKeyAndStartSession() : Error starting session with device $userId:$deviceId")
                } else {
                    Timber.tag(loggerTag.value).d("verifyKeyAndStartSession() : Started new sessionId $sessionId for device $userId:$deviceId")
                }
            } else {
                Timber.tag(loggerTag.value).e("verifyKeyAndStartSession() : Unable to verify otk signature for $userId:$deviceId: $errorMessage")
            }
        }

        return sessionId
    }
}
