

package org.matrix.android.sdk.internal.session.sync.parsing

import com.squareup.moshi.Moshi
import okio.buffer
import okio.source
import org.matrix.android.sdk.api.session.sync.InitialSyncStrategy
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import org.matrix.android.sdk.internal.session.sync.RoomSyncEphemeralTemporaryStore
import timber.log.Timber
import java.io.File
import javax.inject.Inject

internal class InitialSyncResponseParser @Inject constructor(
        private val moshi: Moshi,
        private val roomSyncEphemeralTemporaryStore: RoomSyncEphemeralTemporaryStore
) {

    fun parse(syncStrategy: InitialSyncStrategy.Optimized, workingFile: File): SyncResponse {
        val syncResponseLength = workingFile.length().toInt()
        Timber.d("INIT_SYNC Sync file size is $syncResponseLength bytes")
        val shouldSplit = syncResponseLength >= syncStrategy.minSizeToSplit
        Timber.d("INIT_SYNC should split in several files: $shouldSplit")
        return getMoshi(syncStrategy, shouldSplit)
                .adapter(SyncResponse::class.java)
                .fromJson(workingFile.source().buffer())!!
    }

    private fun getMoshi(syncStrategy: InitialSyncStrategy.Optimized, shouldSplit: Boolean): Moshi {
        
        if (!shouldSplit) return moshi
        
        return moshi.newBuilder()
                .add(SplitLazyRoomSyncEphemeralJsonAdapter(roomSyncEphemeralTemporaryStore, syncStrategy))
                .build()
    }
}
