

package org.matrix.android.sdk.internal.session.room.send

import android.content.Context
import androidx.work.WorkerParameters
import com.squareup.moshi.JsonClass
import io.realm.RealmConfiguration
import org.matrix.android.sdk.api.failure.shouldBeRetried
import org.matrix.android.sdk.api.session.crypto.CryptoService
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.crypto.tasks.SendEventTask
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.session.SessionComponent
import org.matrix.android.sdk.internal.util.toMatrixErrorStr
import org.matrix.android.sdk.internal.worker.SessionSafeCoroutineWorker
import org.matrix.android.sdk.internal.worker.SessionWorkerParams
import timber.log.Timber
import javax.inject.Inject



internal class SendEventWorker(context: Context, params: WorkerParameters, sessionManager: SessionManager) :
        SessionSafeCoroutineWorker<SendEventWorker.Params>(context, params, sessionManager, Params::class.java) {

    @JsonClass(generateAdapter = true)
    internal data class Params(
            override val sessionId: String,
            override val lastFailureMessage: String? = null,
            val eventId: String,
            
            val isEncrypted: Boolean? = null
    ) : SessionWorkerParams

    @Inject lateinit var localEchoRepository: LocalEchoRepository
    @Inject lateinit var sendEventTask: SendEventTask
    @Inject lateinit var cryptoService: CryptoService
    @Inject lateinit var cancelSendTracker: CancelSendTracker
    @SessionDatabase @Inject lateinit var realmConfiguration: RealmConfiguration

    override fun injectWith(injector: SessionComponent) {
        injector.inject(this)
    }

    override suspend fun doSafeWork(params: Params): Result {
        val event = localEchoRepository.getUpToDateEcho(params.eventId)
        if (event?.eventId == null || event.roomId == null) {
            localEchoRepository.updateSendState(params.eventId, event?.roomId, SendState.UNDELIVERED)
            return Result.success()
                    .also { Timber.e("Work cancelled due to bad input data") }
        }

        if (cancelSendTracker.isCancelRequestedFor(params.eventId, event.roomId)) {
            return Result.success()
                    .also {
                        cancelSendTracker.markCancelled(event.eventId, event.roomId)
                        Timber.e("## SendEvent: Event sending has been cancelled ${params.eventId}")
                    }
        }

        if (params.lastFailureMessage != null) {
            localEchoRepository.updateSendState(
                    eventId = event.eventId,
                    roomId = event.roomId,
                    sendState = SendState.UNDELIVERED,
                    sendStateDetails = params.lastFailureMessage
            )
            
            return Result.success(inputData)
                    .also { Timber.e("Work cancelled due to input error from parent") }
        }

        Timber.v("## SendEvent: [${System.currentTimeMillis()}] Send event ${params.eventId}")
        return try {
            sendEventTask.execute(SendEventTask.Params(event, params.isEncrypted ?: cryptoService.isRoomEncrypted(event.roomId)))
            Result.success()
        } catch (exception: Throwable) {
            if ( !exception.shouldBeRetried()) {
                Timber.e("## SendEvent: [${System.currentTimeMillis()}]  Send event Failed cannot retry ${params.eventId} > ${exception.localizedMessage}")
                localEchoRepository.updateSendState(
                        eventId = event.eventId,
                        roomId = event.roomId,
                        sendState = SendState.UNDELIVERED,
                        sendStateDetails = exception.toMatrixErrorStr()
                )
                Result.success()
            } else {
                Timber.e("## SendEvent: [${System.currentTimeMillis()}]  Send event Failed schedule retry ${params.eventId} > ${exception.localizedMessage}")
                Result.retry()
            }
        }
    }

    override fun buildErrorParams(params: Params, message: String): Params {
        return params.copy(lastFailureMessage = params.lastFailureMessage ?: message)
    }
}
