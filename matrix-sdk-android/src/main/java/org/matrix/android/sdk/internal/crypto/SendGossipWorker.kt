

package org.matrix.android.sdk.internal.crypto

import android.content.Context
import androidx.work.WorkerParameters
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.failure.shouldBeRetried
import org.matrix.android.sdk.api.session.crypto.model.GossipingRequestState
import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.content.SecretSendEventContent
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.crypto.actions.EnsureOlmSessionsForDevicesAction
import org.matrix.android.sdk.internal.crypto.actions.MessageEncrypter
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.crypto.tasks.SendToDeviceTask
import org.matrix.android.sdk.internal.crypto.tasks.createUniqueTxnId
import org.matrix.android.sdk.internal.session.SessionComponent
import org.matrix.android.sdk.internal.worker.SessionSafeCoroutineWorker
import org.matrix.android.sdk.internal.worker.SessionWorkerParams
import timber.log.Timber
import javax.inject.Inject

internal class SendGossipWorker(
        context: Context,
        params: WorkerParameters,
        sessionManager: SessionManager
) : SessionSafeCoroutineWorker<SendGossipWorker.Params>(context, params, sessionManager, Params::class.java) {

    @JsonClass(generateAdapter = true)
    internal data class Params(
            override val sessionId: String,
            val secretValue: String,
            val requestUserId: String?,
            val requestDeviceId: String?,
            val requestId: String?,
            
            
            val txnId: String? = null,
            override val lastFailureMessage: String? = null
    ) : SessionWorkerParams

    @Inject lateinit var sendToDeviceTask: SendToDeviceTask
    @Inject lateinit var cryptoStore: IMXCryptoStore
    @Inject lateinit var credentials: Credentials
    @Inject lateinit var messageEncrypter: MessageEncrypter
    @Inject lateinit var ensureOlmSessionsForDevicesAction: EnsureOlmSessionsForDevicesAction

    override fun injectWith(injector: SessionComponent) {
        injector.inject(this)
    }

    override suspend fun doSafeWork(params: Params): Result {
        
        
        
        val txnId = params.txnId ?: createUniqueTxnId()
        val eventType: String = EventType.SEND_SECRET

        val toDeviceContent = SecretSendEventContent(
                requestId = params.requestId ?: "",
                secretValue = params.secretValue
        )

        val requestingUserId = params.requestUserId ?: ""
        val requestingDeviceId = params.requestDeviceId ?: ""
        val deviceInfo = cryptoStore.getUserDevice(requestingUserId, requestingDeviceId)
                ?: return buildErrorResult(params, "Unknown deviceInfo, cannot send message").also {
                    cryptoStore.updateGossipingRequestState(
                            requestUserId = params.requestUserId,
                            requestDeviceId = params.requestDeviceId,
                            requestId = params.requestId,
                            state = GossipingRequestState.FAILED_TO_ACCEPTED
                    )
                    Timber.e("Unknown deviceInfo, cannot send message, sessionId: ${params.requestDeviceId}")
                }

        val sendToDeviceMap = MXUsersDevicesMap<Any>()

        val devicesByUser = mapOf(requestingUserId to listOf(deviceInfo))
        val usersDeviceMap = ensureOlmSessionsForDevicesAction.handle(devicesByUser)
        val olmSessionResult = usersDeviceMap.getObject(requestingUserId, requestingDeviceId)
        if (olmSessionResult?.sessionId == null) {
            
            
            return buildErrorResult(params, "no session with this device").also {
                cryptoStore.updateGossipingRequestState(
                        requestUserId = params.requestUserId,
                        requestDeviceId = params.requestDeviceId,
                        requestId = params.requestId,
                        state = GossipingRequestState.FAILED_TO_ACCEPTED
                )
                Timber.e("no session with this device $requestingDeviceId, probably because there were no one-time keys.")
            }
        }

        val payloadJson = mapOf(
                "type" to EventType.SEND_SECRET,
                "content" to toDeviceContent.toContent()
        )

        try {
            val encodedPayload = messageEncrypter.encryptMessage(payloadJson, listOf(deviceInfo))
            sendToDeviceMap.setObject(requestingUserId, requestingDeviceId, encodedPayload)
        } catch (failure: Throwable) {
            Timber.e("## Fail to encrypt gossip + ${failure.localizedMessage}")
        }

        cryptoStore.saveGossipingEvent(Event(
                type = eventType,
                content = toDeviceContent.toContent(),
                senderId = credentials.userId
        ).also {
            it.ageLocalTs = System.currentTimeMillis()
        })

        try {
            sendToDeviceTask.execute(
                    SendToDeviceTask.Params(
                            eventType = EventType.ENCRYPTED,
                            contentMap = sendToDeviceMap,
                            transactionId = txnId
                    )
            )
            cryptoStore.updateGossipingRequestState(
                    requestUserId = params.requestUserId,
                    requestDeviceId = params.requestDeviceId,
                    requestId = params.requestId,
                    state = GossipingRequestState.ACCEPTED
            )
            return Result.success()
        } catch (throwable: Throwable) {
            return if (throwable.shouldBeRetried()) {
                Result.retry()
            } else {
                cryptoStore.updateGossipingRequestState(
                        requestUserId = params.requestUserId,
                        requestDeviceId = params.requestDeviceId,
                        requestId = params.requestId,
                        state = GossipingRequestState.FAILED_TO_ACCEPTED
                )
                buildErrorResult(params, throwable.localizedMessage ?: "error")
            }
        }
    }

    override fun buildErrorParams(params: Params, message: String): Params {
        return params.copy(lastFailureMessage = params.lastFailureMessage ?: message)
    }
}
