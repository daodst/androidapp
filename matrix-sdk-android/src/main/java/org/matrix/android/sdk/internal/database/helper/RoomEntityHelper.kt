

package org.matrix.android.sdk.internal.database.helper

import org.matrix.android.sdk.internal.database.model.ChunkEntity
import org.matrix.android.sdk.internal.database.model.RoomEntity
import org.matrix.android.sdk.internal.database.model.threads.ThreadSummaryEntity

internal fun RoomEntity.addIfNecessary(chunkEntity: ChunkEntity) {
    if (!chunks.contains(chunkEntity)) {
        chunks.add(chunkEntity)
    }
}

internal fun RoomEntity.addIfNecessary(threadSummary: ThreadSummaryEntity) {
    if (!threadSummaries.contains(threadSummary)) {
        threadSummaries.add(threadSummary)
    }
}
