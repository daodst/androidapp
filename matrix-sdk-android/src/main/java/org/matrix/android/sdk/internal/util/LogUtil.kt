

package org.matrix.android.sdk.internal.util

import org.matrix.android.sdk.BuildConfig
import org.matrix.android.sdk.api.logger.LoggerTag
import timber.log.Timber

internal fun <T> Collection<T>.logLimit(maxQuantity: Int = 5): String {
    return buildString {
        append(size)
        append(" item(s)")
        if (size > maxQuantity) {
            append(", first $maxQuantity items")
        }
        append(": ")
        append(this@logLimit.take(maxQuantity))
    }
}

internal suspend fun <T> logDuration(message: String,
                                     loggerTag: LoggerTag,
                                     block: suspend () -> T): T {
    Timber.tag(loggerTag.value).d("$message -- BEGIN")
    val start = System.currentTimeMillis()
    val result = logRamUsage(message) {
        block()
    }
    val duration = System.currentTimeMillis() - start
    Timber.tag(loggerTag.value).d("$message -- END duration: $duration ms")

    return result
}

internal suspend fun <T> logRamUsage(message: String, block: suspend () -> T): T {
    return if (BuildConfig.DEBUG) {
        val runtime = Runtime.getRuntime()
        runtime.gc()
        val freeMemoryInMb = runtime.freeMemory() / 1048576L
        val usedMemInMBStart = runtime.totalMemory() / 1048576L - freeMemoryInMb
        Timber.d("$message -- BEGIN (free memory: $freeMemoryInMb MB)")
        val result = block()
        runtime.gc()
        val usedMemInMBEnd = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
        val usedMemInMBDiff = usedMemInMBEnd - usedMemInMBStart
        Timber.d("$message -- END RAM usage: $usedMemInMBDiff MB")
        result
    } else {
        block()
    }
}
