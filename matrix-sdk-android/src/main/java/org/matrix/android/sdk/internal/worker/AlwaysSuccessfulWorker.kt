
package org.matrix.android.sdk.internal.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

internal class AlwaysSuccessfulWorker(context: Context, params: WorkerParameters) :
        Worker(context, params) {

    override fun doWork(): Result {
        return Result.success()
    }
}
