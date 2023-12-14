

package org.matrix.android.sdk.internal.session.initsync

import org.matrix.android.sdk.api.session.initsync.InitSyncStep
import timber.log.Timber

internal class TaskInfo(val initSyncStep: InitSyncStep,
                        val totalProgress: Int,
                        val parent: TaskInfo?,
                        val parentWeight: Float) {
    var child: TaskInfo? = null
    var currentProgress = 0F
        private set
    private val offset = parent?.currentProgress ?: 0F

    
    fun leaf(): TaskInfo {
        var last = this
        while (last.child != null) {
            last = last.child!!
        }
        return last
    }

    
    fun setProgress(progress: Float) {
        Timber.v("setProgress: $progress / $totalProgress")
        currentProgress = progress

        parent?.let {
            val parentProgress = (currentProgress / totalProgress) * (parentWeight * it.totalProgress)
            it.setProgress(offset + parentProgress)
        }
    }
}
