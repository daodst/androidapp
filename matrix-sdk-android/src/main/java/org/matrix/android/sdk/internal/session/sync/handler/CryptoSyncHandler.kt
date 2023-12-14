

package org.matrix.android.sdk.internal.session.sync.handler

import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import org.matrix.android.sdk.api.session.crypto.model.MXEventDecryptionResult
import org.matrix.android.sdk.api.session.crypto.model.OlmDecryptionResult
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.content.OlmEventContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import org.matrix.android.sdk.api.session.sync.model.ToDeviceSyncResponse
import org.matrix.android.sdk.internal.crypto.DefaultCryptoService
import org.matrix.android.sdk.internal.crypto.verification.DefaultVerificationService
import org.matrix.android.sdk.internal.session.initsync.ProgressReporter
import timber.log.Timber
import javax.inject.Inject

private val loggerTag = LoggerTag("CryptoSyncHandler", LoggerTag.CRYPTO)

internal class CryptoSyncHandler @Inject constructor(private val cryptoService: DefaultCryptoService,
                                                     private val verificationService: DefaultVerificationService) {

    suspend fun handleToDevice(toDevice: ToDeviceSyncResponse, progressReporter: ProgressReporter? = null) {
        val total = toDevice.events?.size ?: 0
        toDevice.events?.forEachIndexed { index, event ->
            progressReporter?.reportProgress(index * 100F / total)
            
            Timber.tag(loggerTag.value).i("To device event from ${event.senderId} of type:${event.type}")
            decryptToDeviceEvent(event, null)
            if (event.getClearType() == EventType.MESSAGE &&
                    event.getClearContent()?.toModel<MessageContent>()?.msgType == "m.bad.encrypted") {
                Timber.tag(loggerTag.value).e("handleToDeviceEvent() : Warning: Unable to decrypt to-device event : ${event.content}")
            } else {
                verificationService.onToDeviceEvent(event)
                cryptoService.onToDeviceEvent(event)
            }
        }
    }

    fun onSyncCompleted(syncResponse: SyncResponse) {
        cryptoService.onSyncCompleted(syncResponse)
    }

    
    private suspend fun decryptToDeviceEvent(event: Event, timelineId: String?): Boolean {
        Timber.v("## CRYPTO | decryptToDeviceEvent")
        if (event.getClearType() == EventType.ENCRYPTED) {
            var result: MXEventDecryptionResult? = null
            try {
                result = cryptoService.decryptEvent(event, timelineId ?: "")
            } catch (exception: MXCryptoError) {
                event.mCryptoError = (exception as? MXCryptoError.Base)?.errorType 
                val senderKey = event.content.toModel<OlmEventContent>()?.senderKey ?: "<unknown sender key>"
                
                val deviceId = cryptoService.getCryptoDeviceInfo(event.senderId!!).firstOrNull {
                    it.identityKey() == senderKey
                }?.deviceId ?: senderKey
                Timber.e("## CRYPTO | Failed to decrypt to device event from ${event.senderId}|$deviceId reason:<${event.mCryptoError ?: exception}>")
            } catch (failure: Throwable) {
                Timber.e(failure, "## CRYPTO | Failed to decrypt to device event from ${event.senderId}")
            }

            if (null != result) {
                event.mxDecryptionResult = OlmDecryptionResult(
                        payload = result.clearEvent,
                        senderKey = result.senderCurve25519Key,
                        keysClaimed = result.claimedEd25519Key?.let { mapOf("ed25519" to it) },
                        forwardingCurve25519KeyChain = result.forwardingCurve25519KeyChain
                )
                return true
            } else {
                
                
                
                Timber.e("## CRYPTO | ERROR NULL DECRYPTION RESULT from ${event.senderId}")
            }
        }

        return false
    }
}
