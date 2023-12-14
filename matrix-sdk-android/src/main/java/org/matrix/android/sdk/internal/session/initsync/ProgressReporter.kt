

package org.matrix.android.sdk.internal.session.initsync

import org.matrix.android.sdk.api.session.initsync.InitSyncStep

internal interface ProgressReporter {
    fun startTask(initSyncStep: InitSyncStep,
                  totalProgress: Int,
                  parentWeight: Float)

    fun reportProgress(progress: Float)

    fun endTask()
}
