

package org.matrix.android.sdk.api.session.room.threads.local

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent


interface ThreadsLocalService {

    
    fun getAllThreadsLive(): LiveData<List<TimelineEvent>>

    
    fun getAllThreads(): List<TimelineEvent>

    
    fun getMarkedThreadNotificationsLive(): LiveData<List<TimelineEvent>>

    
    fun getMarkedThreadNotifications(): List<TimelineEvent>

    
    fun isUserParticipatingInThread(rootThreadEventId: String): Boolean

    
    fun mapEventsWithEdition(threads: List<TimelineEvent>): List<TimelineEvent>

    
    suspend fun markThreadAsRead(rootThreadEventId: String)
}
