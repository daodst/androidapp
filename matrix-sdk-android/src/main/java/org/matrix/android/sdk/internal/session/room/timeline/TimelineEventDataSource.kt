

package org.matrix.android.sdk.internal.session.room.timeline

import androidx.lifecycle.LiveData
import com.zhuinden.monarchy.Monarchy
import io.realm.Sort
import org.matrix.android.sdk.api.session.events.model.isImageMessage
import org.matrix.android.sdk.api.session.events.model.isVideoMessage
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.util.Optional
import org.matrix.android.sdk.internal.database.RealmSessionProvider
import org.matrix.android.sdk.internal.database.mapper.TimelineEventMapper
import org.matrix.android.sdk.internal.database.model.TimelineEventEntity
import org.matrix.android.sdk.internal.database.model.TimelineEventEntityFields
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.database.query.whereRoomId
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.task.TaskExecutor
import javax.inject.Inject

internal class TimelineEventDataSource @Inject constructor(private val realmSessionProvider: RealmSessionProvider,
                                                           private val timelineEventMapper: TimelineEventMapper,
                                                           private val taskExecutor: TaskExecutor,
                                                           @SessionDatabase private val monarchy: Monarchy) {

    fun getTimelineEvent(roomId: String, eventId: String): TimelineEvent? {
        return realmSessionProvider.withRealm { realm ->
            TimelineEventEntity.where(realm, roomId = roomId, eventId = eventId).findFirst()?.let {
                timelineEventMapper.map(it)
            }
        }
    }

    fun getTimelineEventLive(roomId: String, eventId: String): LiveData<Optional<TimelineEvent>> {
        return LiveTimelineEvent(monarchy, taskExecutor.executorScope, timelineEventMapper, roomId, eventId)
    }

    fun getAttachmentMessages(roomId: String): List<TimelineEvent> {
        
        return realmSessionProvider.withRealm { realm ->
            TimelineEventEntity.whereRoomId(realm, roomId)
                    .sort(TimelineEventEntityFields.DISPLAY_INDEX, Sort.ASCENDING)
                    .findAll()
                    ?.mapNotNull { timelineEventMapper.map(it).takeIf { it.root.isImageMessage() || it.root.isVideoMessage() } }
                    .orEmpty()
        }
    }
}
