

package org.matrix.android.sdk.internal.session.initsync

import org.matrix.android.sdk.api.session.initsync.InitSyncStep

internal inline fun <T> reportSubtask(reporter: ProgressReporter?,
                                      initSyncStep: InitSyncStep,
                                      totalProgress: Int,
                                      parentWeight: Float,
                                      block: () -> T): T {
    reporter?.startTask(initSyncStep, totalProgress, parentWeight)
    return block().also {
        reporter?.endTask()
    }
}

internal inline fun <K, V, R> Map<out K, V>.mapWithProgress(reporter: ProgressReporter?,
                                                            initSyncStep: InitSyncStep,
                                                            parentWeight: Float,
                                                            transform: (Map.Entry<K, V>) -> R): List<R> {
    var current = 0F
    reporter?.startTask(initSyncStep, count() + 1, parentWeight)
    return map {
        reporter?.reportProgress(current)
        current++
        transform.invoke(it)
    }.also {
        reporter?.endTask()
    }
}
