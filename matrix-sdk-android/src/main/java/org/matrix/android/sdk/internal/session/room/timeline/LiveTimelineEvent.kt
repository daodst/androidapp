

package org.matrix.android.sdk.internal.session.room.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import io.realm.RealmQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.events.model.LocalEcho
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.util.Optional
import org.matrix.android.sdk.api.util.toOptional
import org.matrix.android.sdk.internal.database.mapper.TimelineEventMapper
import org.matrix.android.sdk.internal.database.model.TimelineEventEntity
import org.matrix.android.sdk.internal.database.model.TimelineEventEntityFields
import org.matrix.android.sdk.internal.database.query.where


internal class LiveTimelineEvent(private val monarchy: Monarchy,
                                 private val coroutineScope: CoroutineScope,
                                 private val timelineEventMapper: TimelineEventMapper,
                                 private val roomId: String,
                                 private val eventId: String) :
        MediatorLiveData<Optional<TimelineEvent>>() {

    init {
        buildAndObserveQuery()
    }

    private var initialLiveData: LiveData<List<TimelineEvent>>? = null

    
    private fun buildAndObserveQuery() = coroutineScope.launch(Dispatchers.Main) {
        val liveData = monarchy.findAllMappedWithChanges(
                { TimelineEventEntity.where(it, roomId = roomId, eventId = eventId) },
                { timelineEventMapper.map(it) }
        )
        addSource(liveData) { newValue ->
            value = newValue.firstOrNull().toOptional()
        }
        initialLiveData = liveData
        if (LocalEcho.isLocalEchoId(eventId)) {
            observeTimelineEventWithTxId()
        }
    }

    private fun observeTimelineEventWithTxId() {
        val liveData = monarchy.findAllMappedWithChanges(
                { it.queryTimelineEventWithTxId() },
                { timelineEventMapper.map(it) }
        )
        addSource(liveData) { newValue ->
            val optionalValue = newValue.firstOrNull().toOptional()
            if (optionalValue.hasValue()) {
                initialLiveData?.also { removeSource(it) }
                value = optionalValue
            }
        }
    }

    private fun Realm.queryTimelineEventWithTxId(): RealmQuery<TimelineEventEntity> {
        return where(TimelineEventEntity::class.java)
                .equalTo(TimelineEventEntityFields.ROOM_ID, roomId)
                .like(TimelineEventEntityFields.ROOT.UNSIGNED_DATA, """{*"transaction_id":*"$eventId"*}""")
    }
}
