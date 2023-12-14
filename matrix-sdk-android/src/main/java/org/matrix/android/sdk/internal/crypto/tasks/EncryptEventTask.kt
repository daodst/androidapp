
package org.matrix.android.sdk.internal.crypto.tasks

import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.api.session.crypto.CryptoService
import org.matrix.android.sdk.api.session.crypto.model.MXEncryptEventContentResult
import org.matrix.android.sdk.api.session.crypto.model.MXEventDecryptionResult
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.util.awaitCallback
import org.matrix.android.sdk.internal.database.mapper.ContentMapper
import org.matrix.android.sdk.internal.session.room.send.LocalEchoRepository
import org.matrix.android.sdk.internal.task.Task
import timber.log.Timber
import javax.inject.Inject

internal interface EncryptEventTask : Task<EncryptEventTask.Params, Event> {
    data class Params(val roomId: String,
                      val event: Event,
                      
                      val keepKeys: List<String>? = null
    )
}

internal class DefaultEncryptEventTask @Inject constructor(
        private val localEchoRepository: LocalEchoRepository,
        private val cryptoService: CryptoService
) : EncryptEventTask {
    override suspend fun execute(params: EncryptEventTask.Params): Event {
        
        
        val localEvent = params.event
        if (localEvent.eventId == null || localEvent.type == null) {
            throw IllegalArgumentException()
        }

        localEchoRepository.updateSendState(localEvent.eventId, localEvent.roomId, SendState.ENCRYPTING)

        val localMutableContent = localEvent.content?.toMutableMap() ?: mutableMapOf()
        params.keepKeys?.forEach {
            localMutableContent.remove(it)
        }

        
        Timber.i("cryptoService=$cryptoService")
        awaitCallback<MXEncryptEventContentResult> {
            cryptoService.encryptEventContent(localMutableContent, localEvent.type, params.roomId, it)
        }.let { result ->
            val modifiedContent = HashMap(result.eventContent)
            params.keepKeys?.forEach { toKeep ->
                localEvent.content?.get(toKeep)?.let {
                    
                    modifiedContent[toKeep] = it
                }
            }
            val safeResult = result.copy(eventContent = modifiedContent)
            
            
            val decryptionLocalEcho = if (result.eventContent["algorithm"] == MXCRYPTO_ALGORITHM_MEGOLM) {
                MXEventDecryptionResult(
                        clearEvent = Event(
                                type = localEvent.type,
                                content = localEvent.content,
                                roomId = localEvent.roomId
                        ).toContent(),
                        forwardingCurve25519KeyChain = emptyList(),
                        senderCurve25519Key = result.eventContent["sender_key"] as? String,
                        claimedEd25519Key = cryptoService.getMyDevice().fingerprint()
                )
            } else {
                null
            }

            localEchoRepository.updateEcho(localEvent.eventId) { _, localEcho ->
                localEcho.type = EventType.ENCRYPTED
                localEcho.content = ContentMapper.map(modifiedContent)
                decryptionLocalEcho?.also {
                    localEcho.setDecryptionResult(it)
                }
            }
            return localEvent.copy(
                    type = safeResult.eventType,
                    content = safeResult.eventContent
            )
        }
    }
}
