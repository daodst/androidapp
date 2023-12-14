
package org.matrix.android.sdk.internal.session.sync.job

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkerParameters
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.failure.isTokenError
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.di.WorkManagerProvider
import org.matrix.android.sdk.internal.session.SessionComponent
import org.matrix.android.sdk.internal.session.sync.SyncPresence
import org.matrix.android.sdk.internal.session.sync.SyncTask
import org.matrix.android.sdk.internal.worker.SessionSafeCoroutineWorker
import org.matrix.android.sdk.internal.worker.SessionWorkerParams
import org.matrix.android.sdk.internal.worker.WorkerParamsFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val DEFAULT_LONG_POOL_TIMEOUT_SECONDS = 6L
private const val DEFAULT_DELAY_MILLIS = 30_000L


internal class SyncWorker(context: Context, workerParameters: WorkerParameters, sessionManager: SessionManager) :
        SessionSafeCoroutineWorker<SyncWorker.Params>(context, workerParameters, sessionManager, Params::class.java) {

    @JsonClass(generateAdapter = true)
    internal data class Params(
            override val sessionId: String,
            
            val timeout: Long = DEFAULT_LONG_POOL_TIMEOUT_SECONDS,
            
            val delay: Long = DEFAULT_DELAY_MILLIS,
            val periodic: Boolean = false,
            val forceImmediate: Boolean = false,
            override val lastFailureMessage: String? = null
    ) : SessionWorkerParams

    @Inject lateinit var syncTask: SyncTask
    @Inject lateinit var workManagerProvider: WorkManagerProvider

    override fun injectWith(injector: SessionComponent) {
        injector.inject(this)
    }

    override suspend fun doSafeWork(params: Params): Result {
        Timber.i("Sync work starting")

        return runCatching {
            doSync(if (params.forceImmediate) 0 else params.timeout)
        }.fold(
                { hasToDeviceEvents ->
                    Result.success().also {
                        if (params.periodic) {
                            
                            automaticallyBackgroundSync(
                                    workManagerProvider = workManagerProvider,
                                    sessionId = params.sessionId,
                                    serverTimeoutInSeconds = params.timeout,
                                    delayInSeconds = params.delay,
                                    forceImmediate = hasToDeviceEvents
                            )
                        } else if (hasToDeviceEvents) {
                            
                            requireBackgroundSync(
                                    workManagerProvider = workManagerProvider,
                                    sessionId = params.sessionId,
                                    serverTimeoutInSeconds = 0
                            )
                        }
                    }
                },
                { failure ->
                    if (failure.isTokenError()) {
                        Result.failure()
                    } else {
                        
                        
                        
                        Result.retry()
                    }
                }
        )
    }

    override fun buildErrorParams(params: Params, message: String): Params {
        return params.copy(lastFailureMessage = params.lastFailureMessage ?: message)
    }

    
    private suspend fun doSync(timeout: Long): Boolean {
        val taskParams = SyncTask.Params(timeout * 1000, SyncPresence.Offline, afterPause = false)
        val syncResponse = syncTask.execute(taskParams)
        return syncResponse.toDevice?.events?.isNotEmpty().orFalse()
    }

    companion object {
        private const val BG_SYNC_WORK_NAME = "BG_SYNCP"

        fun requireBackgroundSync(workManagerProvider: WorkManagerProvider,
                                  sessionId: String,
                                  serverTimeoutInSeconds: Long = 0) {
            val data = WorkerParamsFactory.toData(
                    Params(
                            sessionId = sessionId,
                            timeout = serverTimeoutInSeconds,
                            delay = 0L,
                            periodic = false
                    )
            )
            val workRequest = workManagerProvider.matrixOneTimeWorkRequestBuilder<SyncWorker>()
                    .setConstraints(WorkManagerProvider.workConstraints)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, WorkManagerProvider.BACKOFF_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .build()
            workManagerProvider.workManager
                    .enqueueUniqueWork(BG_SYNC_WORK_NAME, ExistingWorkPolicy.APPEND_OR_REPLACE, workRequest)
        }

        fun automaticallyBackgroundSync(workManagerProvider: WorkManagerProvider,
                                        sessionId: String,
                                        serverTimeoutInSeconds: Long = 0,
                                        delayInSeconds: Long = 30,
                                        forceImmediate: Boolean = false) {
            val data = WorkerParamsFactory.toData(
                    Params(
                            sessionId = sessionId,
                            timeout = serverTimeoutInSeconds,
                            delay = delayInSeconds,
                            periodic = true,
                            forceImmediate = forceImmediate
                    )
            )
            val workRequest = workManagerProvider.matrixOneTimeWorkRequestBuilder<SyncWorker>()
                    .setConstraints(WorkManagerProvider.workConstraints)
                    .setInputData(data)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, WorkManagerProvider.BACKOFF_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                    .setInitialDelay(if (forceImmediate) 0 else delayInSeconds, TimeUnit.SECONDS)
                    .build()
            
            workManagerProvider.workManager
                    .enqueueUniqueWork(BG_SYNC_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
        }

        fun stopAnyBackgroundSync(workManagerProvider: WorkManagerProvider) {
            workManagerProvider.workManager
                    .cancelUniqueWork(BG_SYNC_WORK_NAME)
        }
    }
}
