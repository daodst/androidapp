
package org.matrix.android.sdk.api.session.initsync

import androidx.lifecycle.LiveData

interface SyncStatusService {

    fun getSyncStatusLive(): LiveData<Status>

    sealed class Status {
        
        abstract class InitialSyncStatus : Status()

        object Idle : InitialSyncStatus()
        data class InitialSyncProgressing(
                val initSyncStep: InitSyncStep,
                val percentProgress: Int = 0
        ) : InitialSyncStatus()

        
        abstract class IncrementalSyncStatus : Status()

        object IncrementalSyncIdle : IncrementalSyncStatus()
        data class IncrementalSyncParsing(
                val rooms: Int,
                val toDevice: Int
        ) : IncrementalSyncStatus()

        object IncrementalSyncError : IncrementalSyncStatus()
        object IncrementalSyncDone : IncrementalSyncStatus()
    }
}
