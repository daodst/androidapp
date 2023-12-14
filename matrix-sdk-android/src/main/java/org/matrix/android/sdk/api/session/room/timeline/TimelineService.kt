

package org.matrix.android.sdk.api.session.room.timeline

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.util.Optional


interface TimelineService {

    
    fun createTimeline(eventId: String?, settings: TimelineSettings): Timeline

    
    fun getTimelineEvent(eventId: String): TimelineEvent?

    
    fun getTimelineEventLive(eventId: String): LiveData<Optional<TimelineEvent>>

    
    fun getAttachmentMessages(): List<TimelineEvent>
}
