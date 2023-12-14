
package org.matrix.android.sdk.internal.crypto.verification

import android.content.Context
import androidx.work.Data
import androidx.work.WorkerParameters
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.failure.shouldBeRetried
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.crypto.tasks.SendVerificationMessageTask
import org.matrix.android.sdk.internal.session.SessionComponent
import org.matrix.android.sdk.internal.session.room.send.CancelSendTracker
import org.matrix.android.sdk.internal.session.room.send.LocalEchoRepository
import org.matrix.android.sdk.internal.worker.SessionSafeCoroutineWorker
import org.matrix.android.sdk.internal.worker.SessionWorkerParams
import timber.log.Timber
import javax.inject.Inject


internal class SendVerificationMessageWorker(context: Context, params: WorkerParameters, sessionManager: SessionManager) :
        SessionSafeCoroutineWorker<SendVerificationMessageWorker.Params>(context, params, sessionManager, Params::class.java) {

    @JsonClass(generateAdapter = true)
    internal data class Params(
            override val sessionId: String,
            val eventId: String,
            override val lastFailureMessage: String? = null
    ) : SessionWorkerParams

    @Inject lateinit var sendVerificationMessageTask: SendVerificationMessageTask
    @Inject lateinit var localEchoRepository: LocalEchoRepository
    @Inject lateinit var cancelSendTracker: CancelSendTracker

    override fun injectWith(injector: SessionComponent) {
        injector.inject(this)
    }

    override suspend fun doSafeWork(params: Params): Result {
        val localEvent = localEchoRepository.getUpToDateEcho(params.eventId) ?: return buildErrorResult(params, "Event not found")
        val localEventId = localEvent.eventId ?: ""
        val roomId = localEvent.roomId ?: ""

        if (cancelSendTracker.isCancelRequestedFor(localEventId, roomId)) {
            return Result.success()
                    .also {
                        cancelSendTracker.markCancelled(localEventId, roomId)
                        Timber.e("## SendEvent: Event sending has been cancelled $localEventId")
                    }
        }

        return try {
            val resultEventId = sendVerificationMessageTask.execute(
                    SendVerificationMessageTask.Params(
                            event = localEvent
                    )
            )

            Result.success(Data.Builder().putString(localEventId, resultEventId).build())
        } catch (throwable: Throwable) {
            if (throwable.shouldBeRetried()) {
                Result.retry()
            } else {
                buildErrorResult(params, throwable.localizedMessage ?: "error")
            }
        }
    }

    override fun buildErrorParams(params: Params, message: String): Params {
        return params.copy(lastFailureMessage = params.lastFailureMessage ?: message)
    }
}
