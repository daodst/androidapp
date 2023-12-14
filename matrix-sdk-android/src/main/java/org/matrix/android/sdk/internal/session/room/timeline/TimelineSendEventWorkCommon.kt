
package org.matrix.android.sdk.internal.session.room.timeline

import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import org.matrix.android.sdk.api.util.Cancelable
import org.matrix.android.sdk.internal.di.WorkManagerProvider
import org.matrix.android.sdk.internal.util.CancelableWork
import org.matrix.android.sdk.internal.worker.startChain
import java.util.concurrent.TimeUnit
import javax.inject.Inject


internal class TimelineSendEventWorkCommon @Inject constructor(
        private val workManagerProvider: WorkManagerProvider
) {

    fun postWork(roomId: String, workRequest: OneTimeWorkRequest, policy: ExistingWorkPolicy = ExistingWorkPolicy.APPEND_OR_REPLACE): Cancelable {
        workManagerProvider.workManager
                .beginUniqueWork(buildWorkName(roomId), policy, workRequest)
                .enqueue()

        return CancelableWork(workManagerProvider.workManager, workRequest.id)
    }

    inline fun <reified W : ListenableWorker> createWork(data: Data, startChain: Boolean): OneTimeWorkRequest {
        return workManagerProvider.matrixOneTimeWorkRequestBuilder<W>()
                .setConstraints(WorkManagerProvider.workConstraints)
                .startChain(startChain)
                .setInputData(data)
                .setBackoffCriteria(BackoffPolicy.LINEAR, WorkManagerProvider.BACKOFF_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .build()
    }

    private fun buildWorkName(roomId: String): String {
        return "${roomId}_$SEND_WORK"
    }

    companion object {
        private const val SEND_WORK = "SEND_WORK"
    }
}
