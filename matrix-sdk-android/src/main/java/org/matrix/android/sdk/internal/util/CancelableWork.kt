

package org.matrix.android.sdk.internal.util

import androidx.work.WorkManager
import org.matrix.android.sdk.api.util.Cancelable
import java.util.UUID

internal class CancelableWork(private val workManager: WorkManager,
                              private val workId: UUID) : Cancelable {

    override fun cancel() {
        workManager.cancelWorkById(workId)
    }
}
