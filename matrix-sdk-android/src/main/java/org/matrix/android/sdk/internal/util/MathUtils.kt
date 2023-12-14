

package org.matrix.android.sdk.internal.util

import kotlin.math.ceil

internal data class BestChunkSize(
        val numberOfChunks: Int,
        val chunkSize: Int
) {
    fun shouldChunk() = numberOfChunks > 1
}

internal fun computeBestChunkSize(listSize: Int, limit: Int): BestChunkSize {
    return if (listSize <= limit) {
        BestChunkSize(
                numberOfChunks = 1,
                chunkSize = listSize
        )
    } else {
        val numberOfChunks = ceil(listSize / limit.toDouble()).toInt()
        
        val chunkSize = ceil(listSize / numberOfChunks.toDouble()).toInt()

        BestChunkSize(
                numberOfChunks = numberOfChunks,
                chunkSize = chunkSize
        )
    }
}
