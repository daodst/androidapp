

package org.matrix.android.sdk.internal.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.crypto.CancelGossipRequestWorker
import org.matrix.android.sdk.internal.crypto.SendGossipRequestWorker
import org.matrix.android.sdk.internal.crypto.SendGossipWorker
import org.matrix.android.sdk.internal.crypto.crosssigning.UpdateTrustWorker
import org.matrix.android.sdk.internal.crypto.verification.SendVerificationMessageWorker
import org.matrix.android.sdk.internal.di.MatrixScope
import org.matrix.android.sdk.internal.session.content.TranslateBeforeUploadWorker
import org.matrix.android.sdk.internal.session.content.UploadContentWorker
import org.matrix.android.sdk.internal.session.group.GetGroupDataWorker
import org.matrix.android.sdk.internal.session.pushers.AddPusherWorker
import org.matrix.android.sdk.internal.session.room.send.MultipleEventSendingDispatcherWorker
import org.matrix.android.sdk.internal.session.room.send.RedactEventWorker
import org.matrix.android.sdk.internal.session.room.send.SendEventWorker
import org.matrix.android.sdk.internal.session.sync.job.SyncWorker
import timber.log.Timber
import javax.inject.Inject


@MatrixScope
internal class MatrixWorkerFactory @Inject constructor(private val sessionManager: SessionManager) : WorkerFactory() {

    override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
    ): ListenableWorker? {
        Timber.d("MatrixWorkerFactory.createWorker for $workerClassName")
        return when (workerClassName) {
            CheckFactoryWorker::class.java.name                   ->
                CheckFactoryWorker(appContext, workerParameters, true)
            AddPusherWorker::class.java.name                      ->
                AddPusherWorker(appContext, workerParameters, sessionManager)
            CancelGossipRequestWorker::class.java.name            ->
                CancelGossipRequestWorker(appContext, workerParameters, sessionManager)
            GetGroupDataWorker::class.java.name                   ->
                GetGroupDataWorker(appContext, workerParameters, sessionManager)
            MultipleEventSendingDispatcherWorker::class.java.name ->
                MultipleEventSendingDispatcherWorker(appContext, workerParameters, sessionManager)
            RedactEventWorker::class.java.name                    ->
                RedactEventWorker(appContext, workerParameters, sessionManager)
            SendEventWorker::class.java.name                      ->
                SendEventWorker(appContext, workerParameters, sessionManager)
            SendGossipRequestWorker::class.java.name              ->
                SendGossipRequestWorker(appContext, workerParameters, sessionManager)
            SendGossipWorker::class.java.name                     ->
                SendGossipWorker(appContext, workerParameters, sessionManager)
            SendVerificationMessageWorker::class.java.name        ->
                SendVerificationMessageWorker(appContext, workerParameters, sessionManager)
            SyncWorker::class.java.name                           ->
                SyncWorker(appContext, workerParameters, sessionManager)
            UpdateTrustWorker::class.java.name                    ->
                UpdateTrustWorker(appContext, workerParameters, sessionManager)
            UploadContentWorker::class.java.name         ->
                UploadContentWorker(appContext, workerParameters, sessionManager)
            TranslateBeforeUploadWorker::class.java.name ->
                TranslateBeforeUploadWorker(appContext, workerParameters, sessionManager)
            else                                         -> {
                Timber.w("No worker defined on MatrixWorkerFactory for $workerClassName will delegate to default.")
                
                null
            }
        }
    }

    
    class CheckFactoryWorker(context: Context,
                             workerParameters: WorkerParameters,
                             private val isCreatedByMatrixWorkerFactory: Boolean) :
            CoroutineWorker(context, workerParameters) {

        
        constructor(context: Context, workerParameters: WorkerParameters) : this(context,
                workerParameters,
                isCreatedByMatrixWorkerFactory = false)

        override suspend fun doWork(): Result {
            return if (!isCreatedByMatrixWorkerFactory) {
                Result.failure()
            } else {
                Result.success()
            }
        }
    }
}
