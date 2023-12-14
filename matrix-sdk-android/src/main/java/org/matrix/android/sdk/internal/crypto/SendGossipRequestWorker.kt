

package org.matrix.android.sdk.internal.crypto

import android.content.Context
import androidx.work.WorkerParameters
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.failure.shouldBeRetried
import org.matrix.android.sdk.api.session.crypto.model.GossipingToDeviceObject
import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap
import org.matrix.android.sdk.api.session.crypto.model.OutgoingGossipingRequestState
import org.matrix.android.sdk.api.session.crypto.model.OutgoingRoomKeyRequest
import org.matrix.android.sdk.api.session.crypto.model.RoomKeyShareRequest
import org.matrix.android.sdk.api.session.crypto.model.SecretShareRequest
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.crypto.tasks.SendToDeviceTask
import org.matrix.android.sdk.internal.crypto.tasks.createUniqueTxnId
import org.matrix.android.sdk.internal.session.SessionComponent
import org.matrix.android.sdk.internal.worker.SessionSafeCoroutineWorker
import org.matrix.android.sdk.internal.worker.SessionWorkerParams
import timber.log.Timber
import javax.inject.Inject

internal class SendGossipRequestWorker(context: Context, params: WorkerParameters, sessionManager: SessionManager) :
        SessionSafeCoroutineWorker<SendGossipRequestWorker.Params>(context, params, sessionManager, Params::class.java) {

    @JsonClass(generateAdapter = true)
    internal data class Params(
            override val sessionId: String,
            val keyShareRequest: OutgoingRoomKeyRequest? = null,
            val secretShareRequest: OutgoingSecretRequest? = null,
            
            
            val txnId: String? = null,
            override val lastFailureMessage: String? = null
    ) : SessionWorkerParams

    @Inject lateinit var sendToDeviceTask: SendToDeviceTask
    @Inject lateinit var cryptoStore: IMXCryptoStore
    @Inject lateinit var credentials: Credentials

    override fun injectWith(injector: SessionComponent) {
        injector.inject(this)
    }

    override suspend fun doSafeWork(params: Params): Result {
        
        
        
        val txnId = params.txnId ?: createUniqueTxnId()
        val contentMap = MXUsersDevicesMap<Any>()
        val eventType: String
        val requestId: String
        when {
            params.keyShareRequest != null    -> {
                eventType = EventType.ROOM_KEY_REQUEST
                requestId = params.keyShareRequest.requestId
                val toDeviceContent = RoomKeyShareRequest(
                        requestingDeviceId = credentials.deviceId,
                        requestId = params.keyShareRequest.requestId,
                        action = GossipingToDeviceObject.ACTION_SHARE_REQUEST,
                        body = params.keyShareRequest.requestBody
                )
                cryptoStore.saveGossipingEvent(Event(
                        type = eventType,
                        content = toDeviceContent.toContent(),
                        senderId = credentials.userId
                ).also {
                    it.ageLocalTs = System.currentTimeMillis()
                })

                params.keyShareRequest.recipients.forEach { userToDeviceMap ->
                    userToDeviceMap.value.forEach { deviceId ->
                        contentMap.setObject(userToDeviceMap.key, deviceId, toDeviceContent)
                    }
                }
            }
            params.secretShareRequest != null -> {
                eventType = EventType.REQUEST_SECRET
                requestId = params.secretShareRequest.requestId
                val toDeviceContent = SecretShareRequest(
                        requestingDeviceId = credentials.deviceId,
                        requestId = params.secretShareRequest.requestId,
                        action = GossipingToDeviceObject.ACTION_SHARE_REQUEST,
                        secretName = params.secretShareRequest.secretName
                )

                cryptoStore.saveGossipingEvent(Event(
                        type = eventType,
                        content = toDeviceContent.toContent(),
                        senderId = credentials.userId
                ).also {
                    it.ageLocalTs = System.currentTimeMillis()
                })

                params.secretShareRequest.recipients.forEach { userToDeviceMap ->
                    userToDeviceMap.value.forEach { deviceId ->
                        contentMap.setObject(userToDeviceMap.key, deviceId, toDeviceContent)
                    }
                }
            }
            else                              -> {
                return buildErrorResult(params, "Unknown empty gossiping request").also {
                    Timber.e("Unknown empty gossiping request: $params")
                }
            }
        }
        try {
            cryptoStore.updateOutgoingGossipingRequestState(requestId, OutgoingGossipingRequestState.SENDING)
            sendToDeviceTask.execute(
                    SendToDeviceTask.Params(
                            eventType = eventType,
                            contentMap = contentMap,
                            transactionId = txnId
                    )
            )
            cryptoStore.updateOutgoingGossipingRequestState(requestId, OutgoingGossipingRequestState.SENT)
            return Result.success()
        } catch (throwable: Throwable) {
            return if (throwable.shouldBeRetried()) {
                Result.retry()
            } else {
                cryptoStore.updateOutgoingGossipingRequestState(requestId, OutgoingGossipingRequestState.FAILED_TO_SEND)
                buildErrorResult(params, throwable.localizedMessage ?: "error")
            }
        }
    }

    override fun buildErrorParams(params: Params, message: String): Params {
        return params.copy(lastFailureMessage = params.lastFailureMessage ?: message)
    }
}
