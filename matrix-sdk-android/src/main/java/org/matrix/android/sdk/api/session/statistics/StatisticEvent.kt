

package org.matrix.android.sdk.api.session.statistics


sealed interface StatisticEvent {
    
    data class InitialSyncRequest(val requestDurationMs: Int,
                                  val downloadDurationMs: Int,
                                  val treatmentDurationMs: Int,
                                  val nbOfJoinedRooms: Int) : StatisticEvent

    
    data class SyncTreatment(val durationMs: Int,
                             val afterPause: Boolean,
                             val nbOfJoinedRooms: Int) : StatisticEvent
}
