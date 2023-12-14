

package org.matrix.android.sdk.api.session.room.threads

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.session.room.threads.model.ThreadSummary


interface ThreadsService {

    
    fun getAllThreadSummariesLive(): LiveData<List<ThreadSummary>>

    
    fun getAllThreadSummaries(): List<ThreadSummary>

    
    fun enhanceThreadWithEditions(threads: List<ThreadSummary>): List<ThreadSummary>

    
    suspend fun fetchThreadTimeline(rootThreadEventId: String, from: String, limit: Int)

    
    suspend fun fetchThreadSummaries()
}
