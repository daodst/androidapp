
package org.matrix.android.sdk.internal.session.room.send

import android.content.Context
import androidx.work.WorkerParameters
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.SessionComponent
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.worker.SessionSafeCoroutineWorker
import org.matrix.android.sdk.internal.worker.SessionWorkerParams
import org.matrix.android.sdk.internal.worker.WorkerParamsFactory
import javax.inject.Inject


internal class RedactEventWorker(context: Context, params: WorkerParameters, sessionManager: SessionManager) :
        SessionSafeCoroutineWorker<RedactEventWorker.Params>(context, params, sessionManager, Params::class.java) {

    @JsonClass(generateAdapter = true)
    internal data class Params(
            override val sessionId: String,
            val txID: String,
            val roomId: String,
            val eventId: String,
            val reason: String?,
            override val lastFailureMessage: String? = null
    ) : SessionWorkerParams

    @Inject lateinit var roomAPI: RoomAPI
    @Inject lateinit var globalErrorReceiver: GlobalErrorReceiver

    override fun injectWith(injector: SessionComponent) {
        injector.inject(this)
    }

    override suspend fun doSafeWork(params: Params): Result {
        val eventId = params.eventId
        return runCatching {
            executeRequest(globalErrorReceiver) {
                roomAPI.redactEvent(
                        params.txID,
                        params.roomId,
                        eventId,
                        if (params.reason == null) emptyMap() else mapOf("reason" to params.reason)
                )
            }
        }.fold(
                {
                    Result.success()
                },
                {
                    when (it) {
                        is Failure.NetworkConnection -> Result.retry()
                        else                         -> {
                            
                            
                            Result.success(WorkerParamsFactory.toData(params.copy(
                                    lastFailureMessage = it.localizedMessage
                            )))
                        }
                    }
                }
        )
    }

    override fun buildErrorParams(params: Params, message: String): Params {
        return params.copy(lastFailureMessage = params.lastFailureMessage ?: message)
    }
}
