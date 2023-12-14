

package org.matrix.android.sdk.internal.session.room.timeline

import io.realm.OrderedCollectionChangeSet
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.RealmChangeListener
import io.realm.RealmConfiguration
import io.realm.RealmObjectChangeListener
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.coroutines.CompletableDeferred
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings
import org.matrix.android.sdk.api.settings.LightweightSettingsStorage
import org.matrix.android.sdk.internal.database.mapper.EventMapper
import org.matrix.android.sdk.internal.database.mapper.TimelineEventMapper
import org.matrix.android.sdk.internal.database.model.ChunkEntity
import org.matrix.android.sdk.internal.database.model.ChunkEntityFields
import org.matrix.android.sdk.internal.database.model.TimelineEventEntity
import org.matrix.android.sdk.internal.database.model.TimelineEventEntityFields
import org.matrix.android.sdk.internal.session.room.relation.threads.DefaultFetchThreadTimelineTask
import org.matrix.android.sdk.internal.session.room.relation.threads.FetchThreadTimelineTask
import org.matrix.android.sdk.internal.session.sync.handler.room.ThreadsAwarenessHandler
import timber.log.Timber
import java.util.Collections
import java.util.concurrent.atomic.AtomicBoolean


internal class TimelineChunk(
        private val chunkEntity: ChunkEntity,
        private val timelineSettings: TimelineSettings,
        private val roomId: String,
        private val timelineId: String,
        private val fetchThreadTimelineTask: FetchThreadTimelineTask,
        private val eventDecryptor: TimelineEventDecryptor,
        private val paginationTask: PaginationTask,
        private val realmConfiguration: RealmConfiguration,
        private val fetchTokenAndPaginateTask: FetchTokenAndPaginateTask,
        private val timelineEventMapper: TimelineEventMapper,
        private val uiEchoManager: UIEchoManager?,
        private val threadsAwarenessHandler: ThreadsAwarenessHandler,
        private val lightweightSettingsStorage: LightweightSettingsStorage,
        private val initialEventId: String?,
        private val onBuiltEvents: (Boolean) -> Unit,
        private val onEventsDeleted: () -> Unit,
) {

    private val isLastForward = AtomicBoolean(chunkEntity.isLastForward)
    private val isLastBackward = AtomicBoolean(chunkEntity.isLastBackward)
    private var prevChunkLatch: CompletableDeferred<Unit>? = null
    private var nextChunkLatch: CompletableDeferred<Unit>? = null

    private val chunkObjectListener = RealmObjectChangeListener<ChunkEntity> { _, changeSet ->
        if (changeSet == null) return@RealmObjectChangeListener
        if (changeSet.isDeleted.orFalse()) {
            return@RealmObjectChangeListener
        }
        Timber.v("on chunk (${chunkEntity.identifier()}) changed: ${changeSet.changedFields?.joinToString(",")}")
        if (changeSet.isFieldChanged(ChunkEntityFields.IS_LAST_FORWARD)) {
            isLastForward.set(chunkEntity.isLastForward)
        }
        if (changeSet.isFieldChanged(ChunkEntityFields.IS_LAST_BACKWARD)) {
            isLastBackward.set(chunkEntity.isLastBackward)
        }
        if (changeSet.isFieldChanged(ChunkEntityFields.NEXT_CHUNK.`$`)) {
            nextChunk = createTimelineChunk(chunkEntity.nextChunk).also {
                it?.prevChunk = this
            }
            nextChunkLatch?.complete(Unit)
        }
        if (changeSet.isFieldChanged(ChunkEntityFields.PREV_CHUNK.`$`)) {
            prevChunk = createTimelineChunk(chunkEntity.prevChunk).also {
                it?.nextChunk = this
            }
            prevChunkLatch?.complete(Unit)
        }
    }

    private val timelineEventsChangeListener =
            OrderedRealmCollectionChangeListener { results: RealmResults<TimelineEventEntity>, changeSet: OrderedCollectionChangeSet ->
                Timber.v("on timeline events chunk update")
                handleDatabaseChangeSet(results, changeSet)
            }

    private var timelineEventEntities: RealmResults<TimelineEventEntity> = chunkEntity.sortedTimelineEvents(timelineSettings.rootThreadEventId)
    private val builtEvents: MutableList<TimelineEvent> = Collections.synchronizedList(ArrayList())
    private val builtSearchEvents: MutableList<TimelineEvent> = Collections.synchronizedList(ArrayList())
    private val builtEventsIndexes: MutableMap<String, Int> = Collections.synchronizedMap(HashMap<String, Int>())

    private var nextChunk: TimelineChunk? = null
    private var prevChunk: TimelineChunk? = null

    init {
        timelineEventEntities.addChangeListener(timelineEventsChangeListener)
        chunkEntity.addChangeListener(chunkObjectListener)
    }

    fun hasReachedLastForward(): Boolean {
        return if (isLastForward.get()) {
            true
        } else {
            nextChunk?.hasReachedLastForward().orFalse()
        }
    }

    fun builtItems(includesNext: Boolean, includesPrev: Boolean): List<TimelineEvent> {
        val deepBuiltItems = ArrayList<TimelineEvent>(builtEvents.size)
        if (includesNext) {
            val nextEvents = nextChunk?.builtItems(includesNext = true, includesPrev = false).orEmpty()
            deepBuiltItems.addAll(nextEvents)
        }
        deepBuiltItems.addAll(builtEvents)
        if (includesPrev) {
            val prevEvents = prevChunk?.builtItems(includesNext = false, includesPrev = true).orEmpty()
            deepBuiltItems.addAll(prevEvents)
        }
        return deepBuiltItems
    }

    
    suspend fun loadMore(count: Int, direction: Timeline.Direction, fetchOnServerIfNeeded: Boolean = true): LoadMoreResult {
        if (direction == Timeline.Direction.FORWARDS && nextChunk != null) {
            return nextChunk?.loadMore(count, direction, fetchOnServerIfNeeded) ?: LoadMoreResult.FAILURE
        } else if (direction == Timeline.Direction.BACKWARDS && prevChunk != null) {
            return prevChunk?.loadMore(count, direction, fetchOnServerIfNeeded) ?: LoadMoreResult.FAILURE
        }
        Timber.tag("jues_timelineChunk").d("loadMore:\n")
        val loadFromStorage = loadFromStorage(count, direction).also {
            logLoadedFromStorage(it, direction)
        }
        if (loadFromStorage.numberOfEvents == 6) {
            Timber.i("here")
        }

        val offsetCount = count - loadFromStorage.numberOfEvents

        return if (offsetCount == 0) {
            LoadMoreResult.SUCCESS
        } else if (direction == Timeline.Direction.FORWARDS && isLastForward.get()) {
            LoadMoreResult.REACHED_END
        } else if (direction == Timeline.Direction.BACKWARDS && isLastBackward.get()) {
            LoadMoreResult.REACHED_END
        } else if (timelineSettings.isThreadTimeline() && loadFromStorage.threadReachedEnd) {
            LoadMoreResult.REACHED_END
        } else {
            delegateLoadMore(fetchOnServerIfNeeded, offsetCount, direction)
        }
    }

    
    suspend fun loadMoreThread(count: Int, direction: Timeline.Direction = Timeline.Direction.BACKWARDS): LoadMoreResult {
        val rootThreadEventId = timelineSettings.rootThreadEventId ?: return LoadMoreResult.FAILURE
        return if (direction == Timeline.Direction.BACKWARDS) {
            try {
                fetchThreadTimelineTask.execute(FetchThreadTimelineTask.Params(
                        roomId,
                        rootThreadEventId,
                        chunkEntity.prevToken,
                        count
                )).toLoadMoreResult()
            } catch (failure: Throwable) {
                Timber.e(failure, "Failed to fetch thread timeline events from the server")
                LoadMoreResult.FAILURE
            }
        } else {
            LoadMoreResult.FAILURE
        }
    }

    private suspend fun delegateLoadMore(fetchFromServerIfNeeded: Boolean, offsetCount: Int, direction: Timeline.Direction): LoadMoreResult {
        return if (direction == Timeline.Direction.FORWARDS) {
            val nextChunkEntity = chunkEntity.nextChunk
            when {
                nextChunkEntity != null -> {
                    if (nextChunk == null) {
                        nextChunk = createTimelineChunk(nextChunkEntity).also {
                            it?.prevChunk = this
                        }
                    }
                    nextChunk?.loadMore(offsetCount, direction, fetchFromServerIfNeeded) ?: LoadMoreResult.FAILURE
                }
                fetchFromServerIfNeeded -> {
                    fetchFromServer(offsetCount, chunkEntity.nextToken, direction)
                }
                else                    -> {
                    LoadMoreResult.SUCCESS
                }
            }
        } else {
            val prevChunkEntity = chunkEntity.prevChunk
            when {
                prevChunkEntity != null -> {
                    if (prevChunk == null) {
                        prevChunk = createTimelineChunk(prevChunkEntity).also {
                            it?.nextChunk = this
                        }
                    }
                    prevChunk?.loadMore(offsetCount, direction, fetchFromServerIfNeeded) ?: LoadMoreResult.FAILURE
                }
                fetchFromServerIfNeeded -> {
                    fetchFromServer(offsetCount, chunkEntity.prevToken, direction)
                }
                else                    -> {
                    LoadMoreResult.SUCCESS
                }
            }
        }
    }

    
    private fun logLoadedFromStorage(loadedFromStorage: LoadedFromStorage, direction: Timeline.Direction) {
        Timber.v("[" +
                "${if (timelineSettings.isThreadTimeline()) "ThreadTimeLine" else "Timeline"}] Has loaded " +
                "${loadedFromStorage.numberOfEvents} items from storage in $direction " +
                if (timelineSettings.isThreadTimeline() && loadedFromStorage.threadReachedEnd) "[Reached End]" else "")
    }

    fun getBuiltEventIndex(eventId: String, searchInNext: Boolean, searchInPrev: Boolean): Int? {
        val builtEventIndex = builtEventsIndexes[eventId]
        if (builtEventIndex != null) {
            return getOffsetIndex() + builtEventIndex
        }
        if (searchInNext) {
            val nextBuiltEventIndex = nextChunk?.getBuiltEventIndex(eventId, searchInNext = true, searchInPrev = false)
            if (nextBuiltEventIndex != null) {
                return nextBuiltEventIndex
            }
        }
        if (searchInPrev) {
            val prevBuiltEventIndex = prevChunk?.getBuiltEventIndex(eventId, searchInNext = false, searchInPrev = true)
            if (prevBuiltEventIndex != null) {
                return prevBuiltEventIndex
            }
        }
        return null
    }

    fun getBuiltEvent(eventId: String, searchInNext: Boolean, searchInPrev: Boolean): TimelineEvent? {
        val builtEventIndex = builtEventsIndexes[eventId]
        if (builtEventIndex != null) {
            return builtEvents.getOrNull(builtEventIndex)
        }
        if (searchInNext) {
            val nextBuiltEvent = nextChunk?.getBuiltEvent(eventId, searchInNext = true, searchInPrev = false)
            if (nextBuiltEvent != null) {
                return nextBuiltEvent
            }
        }
        if (searchInPrev) {
            val prevBuiltEvent = prevChunk?.getBuiltEvent(eventId, searchInNext = false, searchInPrev = true)
            if (prevBuiltEvent != null) {
                return prevBuiltEvent
            }
        }
        return null
    }

    fun rebuildEvent(eventId: String, builder: (TimelineEvent) -> TimelineEvent?, searchInNext: Boolean, searchInPrev: Boolean): Boolean {
        return tryOrNull {
            val builtIndex = getBuiltEventIndex(eventId, searchInNext = false, searchInPrev = false)
            if (builtIndex == null) {
                val foundInPrev = searchInPrev && prevChunk?.rebuildEvent(eventId, builder, searchInNext = false, searchInPrev = true).orFalse()
                if (foundInPrev) {
                    return true
                }
                if (searchInNext) {
                    return prevChunk?.rebuildEvent(eventId, builder, searchInPrev = false, searchInNext = true).orFalse()
                }
                return false
            }
            
            builtEvents.getOrNull(builtIndex)?.let { te ->
                val rebuiltEvent = builder(te)
                builtEvents[builtIndex] = rebuiltEvent!!
                true
            }
        }
                ?: false
    }

    fun close(closeNext: Boolean, closePrev: Boolean) {
        if (closeNext) {
            nextChunk?.close(closeNext = true, closePrev = false)
        }
        if (closePrev) {
            prevChunk?.close(closeNext = false, closePrev = true)
        }
        nextChunk = null
        nextChunkLatch?.cancel()
        prevChunk = null
        prevChunkLatch?.cancel()
        chunkEntity.removeChangeListener(chunkObjectListener)
        timelineEventEntities.removeChangeListener(timelineEventsChangeListener)
    }

    
    private fun loadFromStorage(count: Int, direction: Timeline.Direction): LoadedFromStorage {
        val displayIndex = getNextDisplayIndex(direction) ?: return LoadedFromStorage()
        val baseQuery = timelineEventEntities.where()

        val timelineEvents = baseQuery
                .offsets(direction, count, displayIndex)
                .findAll()
                .orEmpty()

        if (timelineEvents.isEmpty()) return LoadedFromStorage()
        if (direction == Timeline.Direction.FORWARDS) {
            builtEventsIndexes.entries.forEach { it.setValue(it.value + timelineEvents.size) }
        }
        timelineEvents
            .mapIndexed { index, timelineEventEntity ->
                val timelineEvent = timelineEventEntity.buildAndDecryptIfNeeded()
                if (timelineEvent.root.type == EventType.STATE_ROOM_CREATE) {
                    isLastBackward.set(true)
                }
                if (direction == Timeline.Direction.FORWARDS) {
                    builtEventsIndexes[timelineEvent.eventId] = index
                    builtEvents.add(index, timelineEvent)
                } else {
                    builtEventsIndexes[timelineEvent.eventId] = builtEvents.size
                    builtEvents.add(timelineEvent)
                }
            }
        return LoadedFromStorage(
            threadReachedEnd = threadReachedEnd(timelineEvents),
            numberOfEvents = timelineEvents.size
        )
    }

    
    fun loadFromStorageForAll(
        count: Int,
        direction: Timeline.Direction,
        listener: RealmChangeListener<MutableList<TimelineEvent>>
    ) {
        Timber.tag("jues_search").v("timelineEventEntities.size:${timelineEventEntities.size}")
        
        val baseQuery = timelineEventEntities.where()
        val timelineEvents = baseQuery.sort(TimelineEventEntityFields.DISPLAY_INDEX, Sort.DESCENDING)
            
            .findAllAsync()
        
        

        val listener1 = RealmChangeListener<RealmResults<TimelineEventEntity>> { it ->
            Timber.tag("jues_search").v("listener1.size:${it.size}")
            if (it.isEmpty()) {
                listener.onChange(builtSearchEvents)
                timelineEvents.removeAllChangeListeners()
                return@RealmChangeListener
            }

            it.mapIndexed { index, timelineEventEntity ->
                val timelineEvent = timelineEventEntity.buildAndDecryptIfNeeded()
                if (timelineEvent.root.type == EventType.STATE_ROOM_CREATE) {
                    isLastBackward.set(true)
                }
                if (direction == Timeline.Direction.FORWARDS) {
                    
                    builtSearchEvents.add(index, timelineEvent)
                } else {
                    
                    builtSearchEvents.add(timelineEvent)
                }
            }

            val prevChunkEntity = chunkEntity.prevChunk
            if (prevChunkEntity != null) {
                if (prevChunk == null) {
                    prevChunk = createTimelineChunk(prevChunkEntity).also {
                        it?.nextChunk = this
                    }
                }
                prevChunk?.loadFromStorageForAll(count, direction, listener)
            }
            listener.onChange(builtSearchEvents)
            timelineEvents.removeAllChangeListeners()

        }
        Timber.tag("jues_search").v("listener1.")
        timelineEvents.addChangeListener(listener1)
    }

    
    private fun threadReachedEnd(timelineEvents: List<TimelineEventEntity>): Boolean =
            timelineSettings.rootThreadEventId?.let { rootThreadId ->
                timelineEvents.firstOrNull { it.eventId == rootThreadId }?.let { true }
            } ?: false

    
    private suspend fun fetchRootThreadEventsIfNeeded(offsetResults: List<TimelineEventEntity>) {
        val eventEntityList = offsetResults
                .mapNotNull {
                    it.root
                }.map {
                    EventMapper.map(it)
                }
        threadsAwarenessHandler.fetchRootThreadEventsIfNeeded(eventEntityList)
    }

    private fun TimelineEventEntity.buildAndDecryptIfNeeded(): TimelineEvent {
        val timelineEvent = buildTimelineEvent(this)
        val transactionId = timelineEvent.root.unsignedData?.transactionId
        uiEchoManager?.onSyncedEvent(transactionId)
        if (timelineEvent.isEncrypted() &&
                timelineEvent.root.mxDecryptionResult == null) {
            timelineEvent.root.eventId?.also { eventDecryptor.requestDecryption(TimelineEventDecryptor.DecryptionRequest(timelineEvent.root, timelineId)) }
        }
        if (!timelineEvent.isEncrypted() && !lightweightSettingsStorage.areThreadMessagesEnabled()) {
            
            timelineEvent.root.eventId?.also { eventDecryptor.requestDecryption(TimelineEventDecryptor.DecryptionRequest(timelineEvent.root, timelineId)) }
        }
        return timelineEvent
    }

    private fun buildTimelineEvent(eventEntity: TimelineEventEntity) = timelineEventMapper.map(
            timelineEventEntity = eventEntity,
            buildReadReceipts = timelineSettings.buildReadReceipts
    ).let {
        
        (uiEchoManager?.decorateEventWithReactionUiEcho(it) ?: it)
    }

    
    private suspend fun fetchFromServer(count: Int, token: String?, direction: Timeline.Direction): LoadMoreResult {
        val latch = if (direction == Timeline.Direction.FORWARDS) {
            nextChunkLatch = CompletableDeferred()
            nextChunkLatch
        } else {
            prevChunkLatch = CompletableDeferred()
            prevChunkLatch
        }
        val loadMoreResult = try {
            if (token == null) {
                if (direction == Timeline.Direction.BACKWARDS || !chunkEntity.hasBeenALastForwardChunk()) return LoadMoreResult.REACHED_END
                val lastKnownEventId = chunkEntity.sortedTimelineEvents(timelineSettings.rootThreadEventId).firstOrNull()?.eventId
                        ?: return LoadMoreResult.FAILURE
                val taskParams = FetchTokenAndPaginateTask.Params(roomId, lastKnownEventId, direction.toPaginationDirection(), count)
                fetchTokenAndPaginateTask.execute(taskParams).toLoadMoreResult()
            } else {
                Timber.v("Fetch $count more events on server")
                val taskParams = PaginationTask.Params(roomId, token, direction.toPaginationDirection(), count)
                paginationTask.execute(taskParams).toLoadMoreResult()
            }
        } catch (failure: Throwable) {
            Timber.e(failure, "Failed to fetch from server")
            LoadMoreResult.FAILURE
        }
        return if (loadMoreResult == LoadMoreResult.SUCCESS) {
            latch?.await()
            loadMore(count, direction, fetchOnServerIfNeeded = false)
        } else {
            loadMoreResult
        }
    }

    private fun TokenChunkEventPersistor.Result.toLoadMoreResult(): LoadMoreResult {
        return when (this) {
            TokenChunkEventPersistor.Result.REACHED_END -> LoadMoreResult.REACHED_END
            TokenChunkEventPersistor.Result.SHOULD_FETCH_MORE,
            TokenChunkEventPersistor.Result.SUCCESS     -> LoadMoreResult.SUCCESS
        }
    }

    private fun DefaultFetchThreadTimelineTask.Result.toLoadMoreResult(): LoadMoreResult {
        return when (this) {
            DefaultFetchThreadTimelineTask.Result.REACHED_END -> LoadMoreResult.REACHED_END
            DefaultFetchThreadTimelineTask.Result.SHOULD_FETCH_MORE,
            DefaultFetchThreadTimelineTask.Result.SUCCESS     -> LoadMoreResult.SUCCESS
        }
    }

    private fun getOffsetIndex(): Int {
        var offset = 0
        var currentNextChunk = nextChunk
        while (currentNextChunk != null) {
            offset += currentNextChunk.builtEvents.size
            currentNextChunk = currentNextChunk.nextChunk
        }
        return offset
    }

    
    private fun handleDatabaseChangeSet(results: RealmResults<TimelineEventEntity>, changeSet: OrderedCollectionChangeSet) {
        val insertions = changeSet.insertionRanges
        for (range in insertions) {
            val newItems = results
                    .subList(range.startIndex, range.startIndex + range.length)
                    .map { it.buildAndDecryptIfNeeded() }
            builtEventsIndexes.entries.filter { it.value >= range.startIndex }.forEach { it.setValue(it.value + range.length) }
            newItems.mapIndexed { index, timelineEvent ->
                if (timelineEvent.root.type == EventType.STATE_ROOM_CREATE) {
                    isLastBackward.set(true)
                }
                val correctedIndex = range.startIndex + index
                builtEvents.add(correctedIndex, timelineEvent)
                builtEventsIndexes[timelineEvent.eventId] = correctedIndex
            }
        }
        val modifications = changeSet.changeRanges
        for (range in modifications) {
            for (modificationIndex in (range.startIndex until range.startIndex + range.length)) {
                val updatedEntity = results[modificationIndex] ?: continue
                try {
                    builtEvents[modificationIndex] = updatedEntity.buildAndDecryptIfNeeded()
                } catch (failure: Throwable) {
                    Timber.v("Fail to update items at index: $modificationIndex")
                }
            }
        }

        if (insertions.isNotEmpty() || modifications.isNotEmpty()) {
            onBuiltEvents(true)
        }

        val deletions = changeSet.deletions
        if (deletions.isNotEmpty()) {
            onEventsDeleted()
        }
    }

    private fun getNextDisplayIndex(direction: Timeline.Direction): Int? {
        if (timelineEventEntities.isEmpty()) {
            return null
        }
        return if (builtEvents.isEmpty()) {
            if (initialEventId != null) {
                timelineEventEntities.where().equalTo(TimelineEventEntityFields.EVENT_ID, initialEventId).findFirst()?.displayIndex
            } else if (direction == Timeline.Direction.BACKWARDS) {
                timelineEventEntities.first(null)?.displayIndex
            } else {
                timelineEventEntities.last(null)?.displayIndex
            }
        } else if (direction == Timeline.Direction.FORWARDS) {
            builtEvents.first().displayIndex + 1
        } else {
            builtEvents.last().displayIndex - 1
        }
    }

    private fun createTimelineChunk(chunkEntity: ChunkEntity?): TimelineChunk? {
        if (chunkEntity == null) return null
        return TimelineChunk(
                chunkEntity = chunkEntity,
                timelineSettings = timelineSettings,
                roomId = roomId,
                timelineId = timelineId,
                eventDecryptor = eventDecryptor,
                paginationTask = paginationTask,
                realmConfiguration = realmConfiguration,
                fetchThreadTimelineTask = fetchThreadTimelineTask,
                fetchTokenAndPaginateTask = fetchTokenAndPaginateTask,
                timelineEventMapper = timelineEventMapper,
                uiEchoManager = uiEchoManager,
                threadsAwarenessHandler = threadsAwarenessHandler,
                lightweightSettingsStorage = lightweightSettingsStorage,
                initialEventId = null,
                onBuiltEvents = this.onBuiltEvents,
                onEventsDeleted = this.onEventsDeleted
        )
    }

    private data class LoadedFromStorage(
            val threadReachedEnd: Boolean = false,
            val numberOfEvents: Int = 0
    )
}

private fun RealmQuery<TimelineEventEntity>.offsets(
        direction: Timeline.Direction,
        count: Int,
        startDisplayIndex: Int
): RealmQuery<TimelineEventEntity> {
    return if (direction == Timeline.Direction.BACKWARDS) {
        lessThanOrEqualTo(TimelineEventEntityFields.DISPLAY_INDEX, startDisplayIndex)
        sort(TimelineEventEntityFields.DISPLAY_INDEX, Sort.DESCENDING)
        limit(count.toLong())
    } else {
        greaterThanOrEqualTo(TimelineEventEntityFields.DISPLAY_INDEX, startDisplayIndex)
        
        sort(TimelineEventEntityFields.DISPLAY_INDEX, Sort.ASCENDING)
        limit(count.toLong())
        
        sort(TimelineEventEntityFields.DISPLAY_INDEX, Sort.DESCENDING)
    }
}

private fun Timeline.Direction.toPaginationDirection(): PaginationDirection {
    return if (this == Timeline.Direction.BACKWARDS) PaginationDirection.BACKWARDS else PaginationDirection.FORWARDS
}

private fun ChunkEntity.sortedTimelineEvents(rootThreadEventId: String?): RealmResults<TimelineEventEntity> {
    return if (rootThreadEventId == null) {
        timelineEvents
                .sort(TimelineEventEntityFields.DISPLAY_INDEX, Sort.DESCENDING)
    } else {
        timelineEvents
                .where()
                .beginGroup()
                .equalTo(TimelineEventEntityFields.ROOT.ROOT_THREAD_EVENT_ID, rootThreadEventId)
                .or()
                .equalTo(TimelineEventEntityFields.ROOT.EVENT_ID, rootThreadEventId)
                .endGroup()
                .findAll()
    }
}
