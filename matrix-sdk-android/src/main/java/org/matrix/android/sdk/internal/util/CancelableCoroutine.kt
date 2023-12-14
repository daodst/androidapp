

package org.matrix.android.sdk.internal.util

import kotlinx.coroutines.Job
import org.matrix.android.sdk.api.util.Cancelable

internal fun Job.toCancelable(): Cancelable {
    return CancelableCoroutine(this)
}


private class CancelableCoroutine(private val job: Job) : Cancelable {

    override fun cancel() {
        if (!job.isCancelled) {
            job.cancel()
        }
    }
}
