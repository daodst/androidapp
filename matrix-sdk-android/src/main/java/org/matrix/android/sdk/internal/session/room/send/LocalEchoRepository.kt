

package org.matrix.android.sdk.internal.session.room.send

import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.isLocalMsg
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.internal.database.RealmSessionProvider
import org.matrix.android.sdk.internal.database.asyncTransaction
import org.matrix.android.sdk.internal.database.helper.addIfNecessary
import org.matrix.android.sdk.internal.database.helper.addTimelineEvent
import org.matrix.android.sdk.internal.database.helper.nextDisplayIndex
import org.matrix.android.sdk.internal.database.helper.nextId
import org.matrix.android.sdk.internal.database.mapper.TimelineEventMapper
import org.matrix.android.sdk.internal.database.mapper.asDomain
import org.matrix.android.sdk.internal.database.mapper.toEntity
import org.matrix.android.sdk.internal.database.model.ChunkEntity
import org.matrix.android.sdk.internal.database.model.EventEntity
import org.matrix.android.sdk.internal.database.model.EventInsertEntity
import org.matrix.android.sdk.internal.database.model.EventInsertType
import org.matrix.android.sdk.internal.database.model.RoomEntity
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntity
import org.matrix.android.sdk.internal.database.model.TimelineEventEntity
import org.matrix.android.sdk.internal.database.query.copyToRealmOrIgnore
import org.matrix.android.sdk.internal.database.query.findAllInRoomWithSendStates
import org.matrix.android.sdk.internal.database.query.findLastForwardChunkOfRoom
import org.matrix.android.sdk.internal.database.query.getOrCreate
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.network.GlobalErrorInteractImpl
import org.matrix.android.sdk.internal.session.room.membership.RoomMemberHelper
import org.matrix.android.sdk.internal.session.room.summary.RoomSummaryEventsHelper
import org.matrix.android.sdk.internal.session.room.summary.RoomSummaryUpdater
import org.matrix.android.sdk.internal.session.room.timeline.PaginationDirection
import org.matrix.android.sdk.internal.session.room.timeline.TimelineInput
import org.matrix.android.sdk.internal.task.TaskExecutor
import org.matrix.android.sdk.internal.util.awaitTransaction
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

internal class LocalEchoRepository @Inject constructor(@SessionDatabase private val monarchy: Monarchy, private val taskExecutor: TaskExecutor, private val realmSessionProvider: RealmSessionProvider, private val roomSummaryUpdater: RoomSummaryUpdater, private val timelineInput: TimelineInput, private val interact: GlobalErrorInteractImpl, private val timelineEventMapper: TimelineEventMapper) {

    
    fun createLocalEcho(event: Event) {
        if (event.isLocalMsg()) {
            createLocalEchoCus2(event)
            
        } else {
            createLocalEchoDefault(event)
        }
    }

    private fun createLocalEchoCus3(event: Event) {
        val roomId = event.roomId ?: throw IllegalStateException("You should have set a roomId for your event")
        val senderId = event.senderId ?: throw IllegalStateException("You should have set a senderId for your event")
        event.eventId ?: throw IllegalStateException("You should have set an eventId for your event")
        event.type ?: throw IllegalStateException("You should have set a type for your event")

        var chunkEntity: ChunkEntity? = null

        taskExecutor.executorScope.asyncTransaction(monarchy) { realm ->
            chunkEntity = ChunkEntity.findLastForwardChunkOfRoom(realm, roomId)
            val displayIndex = chunkEntity?.nextDisplayIndex(PaginationDirection.FORWARDS) ?: 0
            
            val eventEntity = event.toEntity(roomId, SendState.SYNCED, System.currentTimeMillis())
            val roomMemberHelper = RoomMemberHelper(realm, roomId)
            val myUser = roomMemberHelper.getLastRoomMember(senderId)
            
            val localId = TimelineEventEntity.nextId(realm)
            val timelineEventEntity = TimelineEventEntity(localId).also {
                it.root = eventEntity
                it.displayIndex = displayIndex
                it.eventId = event.eventId
                it.roomId = roomId
                it.senderName = myUser?.displayName
                it.senderAvatar = myUser?.avatarUrl
                it.isUniqueDisplayName = roomMemberHelper.isUniqueDisplayName(myUser?.displayName)
            }

            val eventInsertEntity = EventInsertEntity(event.eventId, event.type, canBeProcessed = true).apply {
                this.insertType = EventInsertType.INCREMENTAL_SYNC
            }
            realm.insert(eventInsertEntity)

            val roomEntity = RoomEntity.where(realm, roomId = roomId).findFirst() ?: return@asyncTransaction
            chunkEntity?.apply {
                timelineEvents.add(timelineEventEntity)
            }?.also {
                roomEntity.addIfNecessary(it)
            }

            if (false) {
                val eventIds = ArrayList<String>(1)
                eventIds.add(event.eventId)
                timelineInput.onNewTimelineEvents(roomId = roomId, eventIds = eventIds)
            }

        }
    }

    private fun createLocalEchoCus(event: Event) {
        val roomId = event.roomId ?: throw IllegalStateException("You should have set a roomId for your event")
        val senderId = event.senderId ?: throw IllegalStateException("You should have set a senderId for your event")
        event.eventId ?: throw IllegalStateException("You should have set an eventId for your event")
        event.type ?: throw IllegalStateException("You should have set a type for your event")

        var chunkEntity: ChunkEntity? = null

        val timelineEventEntity = realmSessionProvider.withRealm { realm ->
            chunkEntity = ChunkEntity.findLastForwardChunkOfRoom(realm, roomId)
            val displayIndex = chunkEntity?.nextDisplayIndex(PaginationDirection.FORWARDS) ?: 0
            val eventEntity = event.toEntity(roomId, SendState.SYNCED, System.currentTimeMillis())
            val roomMemberHelper = RoomMemberHelper(realm, roomId)
            val myUser = roomMemberHelper.getLastRoomMember(senderId)
            
            val localId = TimelineEventEntity.nextId(realm)
            TimelineEventEntity(localId).also {
                it.root = eventEntity
                it.displayIndex = displayIndex
                it.eventId = event.eventId
                it.roomId = roomId
                it.senderName = myUser?.displayName
                it.senderAvatar = myUser?.avatarUrl
                it.isUniqueDisplayName = roomMemberHelper.isUniqueDisplayName(myUser?.displayName)
            }
        }

        taskExecutor.executorScope.asyncTransaction(monarchy) { realm ->
            val eventInsertEntity = EventInsertEntity(event.eventId, event.type, canBeProcessed = true).apply {
                this.insertType = EventInsertType.INCREMENTAL_SYNC
            }
            realm.insert(eventInsertEntity)

            val roomEntity = RoomEntity.where(realm, roomId = roomId).findFirst() ?: return@asyncTransaction
            if (false) {

                
                roomEntity.sendingTimelineEvents.add(0, timelineEventEntity)
                
            } else {
                chunkEntity?.apply {
                    
                    if (false) {
                        addTimelineEvent(
                                roomId = roomId,
                                eventEntity = timelineEventEntity.root!!,
                                direction = PaginationDirection.FORWARDS,
                        )
                    }
                    timelineEvents.add(timelineEventEntity)
                }?.also {
                    roomEntity.addIfNecessary(it)
                }
            }

            if (false) {
                val eventIds = ArrayList<String>(1)
                eventIds.add(event.eventId)
                timelineInput.onNewTimelineEvents(roomId = roomId, eventIds = eventIds)
            }

        }
    }

    private fun createLocalEchoDefault(event: Event) {
        val roomId = event.roomId ?: throw IllegalStateException("You should have set a roomId for your event")
        val senderId = event.senderId ?: throw IllegalStateException("You should have set a senderId for your event")
        event.eventId ?: throw IllegalStateException("You should have set an eventId for your event")
        event.type ?: throw IllegalStateException("You should have set a type for your event")

        val timelineEventEntity = realmSessionProvider.withRealm { realm ->
            val eventEntity = event.toEntity(roomId, SendState.UNSENT, System.currentTimeMillis())
            val roomMemberHelper = RoomMemberHelper(realm, roomId)
            val myUser = roomMemberHelper.getLastRoomMember(senderId)
            val localId = UUID.randomUUID().mostSignificantBits
            TimelineEventEntity(localId).also {
                it.root = eventEntity
                it.eventId = event.eventId
                it.roomId = roomId
                it.senderName = myUser?.displayName
                it.senderAvatar = myUser?.avatarUrl
                it.isUniqueDisplayName = roomMemberHelper.isUniqueDisplayName(myUser?.displayName)
            }
        }
        val timelineEvent = timelineEventMapper.map(timelineEventEntity)
        timelineInput.onLocalEchoCreated(roomId = roomId, timelineEvent = timelineEvent)
        taskExecutor.executorScope.asyncTransaction(monarchy) { realm ->
            val eventInsertEntity = EventInsertEntity(event.eventId, event.type, canBeProcessed = true).apply {
                this.insertType = EventInsertType.LOCAL_ECHO
            }
            realm.insert(eventInsertEntity)
            val roomEntity = RoomEntity.where(realm, roomId = roomId).findFirst() ?: return@asyncTransaction
            roomEntity.sendingTimelineEvents.add(0, timelineEventEntity)
            roomSummaryUpdater.updateSendingInformation(realm, roomId)
        }
    }

    private fun createLocalEchoCus2(event: Event) {
        val roomId = event.roomId ?: throw IllegalStateException("You should have set a roomId for your event")
        val senderId = event.senderId ?: throw IllegalStateException("You should have set a senderId for your event")
        event.eventId ?: throw IllegalStateException("You should have set an eventId for your event")
        event.type ?: throw IllegalStateException("You should have set a type for your event")
        val eventIds = ArrayList<String>(1)
        eventIds.add(event.eventId)
        taskExecutor.executorScope.asyncTransaction(monarchy) { realm ->
            val roomEntity = RoomEntity.getOrCreate(realm, roomId)
            val roomSummaryEntity = RoomSummaryEntity.getOrCreate(realm, roomId)
            val eventEntity = event.toEntity(roomId, SendState.SYNCED, System.currentTimeMillis()).copyToRealmOrIgnore(realm, EventInsertType.INCREMENTAL_SYNC)
            ChunkEntity.findLastForwardChunkOfRoom(realm, roomId)?.apply {
                addTimelineEvent(
                        roomId = roomId,
                        eventEntity = eventEntity,
                        direction = PaginationDirection.FORWARDS,
                )?.let {
                    
                    roomSummaryEntity.atMsg.add(0, it)
                }
            }?.also {
                roomEntity.addIfNecessary(it)
            }

            val latestPreviewableEvent = RoomSummaryEventsHelper.getLatestPreviewableEvent(realm, roomId)
            roomSummaryEntity.latestPreviewableEvent = latestPreviewableEvent
            timelineInput.onNewTimelineEvents(roomId = roomId, eventIds = eventIds)
        }
    }

    fun updateSendState(eventId: String, roomId: String?, sendState: SendState, sendStateDetails: String? = null) {
        interact.getGlobalErrorInteractHandler().forEach {
            if (null != sendStateDetails && roomId != null) {
                val matrixErrorAdapter = MoshiProvider.providesMoshi().adapter(MatrixError::class.java)
                val error = tryOrNull { matrixErrorAdapter.fromJson(sendStateDetails) }
                if (error?.code == MatrixError.M_TOO_LARGE) {
                    it.handlerThrowable(MatrixError.M_TOO_LARGE, RuntimeException("${error.maxUploadFileSize}"), roomId)
                } else {
                    it.handlerThrowable(RuntimeException(error?.message), roomId)
                }
            }
        }
        Timber.v("## SendEvent: [${System.currentTimeMillis()}] Update local state of $eventId to ${sendState.name}")
        timelineInput.onLocalEchoUpdated(roomId = roomId ?: "", eventId = eventId, sendState = sendState)
        updateEchoAsync(eventId) { realm, sendingEventEntity ->
            if (sendState == SendState.SENT && sendingEventEntity.sendState == SendState.SYNCED) {
                
            } else {
                sendingEventEntity.sendState = sendState
            }
            sendingEventEntity.sendStateDetails = sendStateDetails
            roomSummaryUpdater.updateSendingInformation(realm, sendingEventEntity.roomId)
        }
    }

    suspend fun updateEcho(eventId: String, block: (realm: Realm, eventEntity: EventEntity) -> Unit) {
        monarchy.awaitTransaction { realm ->
            val sendingEventEntity = EventEntity.where(realm, eventId).findFirst()
            if (sendingEventEntity != null) {
                block(realm, sendingEventEntity)
            }
        }
    }

    fun updateEchoAsync(eventId: String, block: (realm: Realm, eventEntity: EventEntity) -> Unit) {
        taskExecutor.executorScope.asyncTransaction(monarchy) { realm ->
            val sendingEventEntity = EventEntity.where(realm, eventId).findFirst()
            if (sendingEventEntity != null) {
                block(realm, sendingEventEntity)
            }
        }
    }

    suspend fun getUpToDateEcho(eventId: String): Event? {
        
        return monarchy.awaitTransaction { realm ->
            EventEntity.where(realm, eventId).findFirst()?.asDomain(castJsonNumbers = true)
        }
    }

    suspend fun deleteFailedEcho(roomId: String, localEcho: TimelineEvent) {
        deleteFailedEcho(roomId, localEcho.eventId)
    }

    suspend fun deleteFailedEcho(roomId: String, eventId: String?) {
        monarchy.awaitTransaction { realm ->
            TimelineEventEntity.where(realm, roomId = roomId, eventId = eventId ?: "").findFirst()?.deleteFromRealm()
            EventEntity.where(realm, eventId = eventId ?: "").findFirst()?.deleteFromRealm()
            roomSummaryUpdater.updateSendingInformation(realm, roomId)
        }
    }

    fun deleteFailedEchoAsync(roomId: String, eventId: String?) {
        monarchy.runTransactionSync { realm ->
            TimelineEventEntity.where(realm, roomId = roomId, eventId = eventId ?: "").findFirst()?.deleteFromRealm()
            EventEntity.where(realm, eventId = eventId ?: "").findFirst()?.deleteFromRealm()
            roomSummaryUpdater.updateSendingInformation(realm, roomId)
        }
    }

    suspend fun clearSendingQueue(roomId: String) {
        monarchy.awaitTransaction { realm ->
            TimelineEventEntity.findAllInRoomWithSendStates(realm, roomId, SendState.IS_SENDING_STATES).forEach {
                it.root?.sendState = SendState.UNSENT
            }
            roomSummaryUpdater.updateSendingInformation(realm, roomId)
        }
    }

    suspend fun updateSendState(roomId: String, eventIds: List<String>, sendState: SendState) {
        monarchy.awaitTransaction { realm ->
            val timelineEvents = TimelineEventEntity.where(realm, roomId, eventIds).findAll()
            timelineEvents.forEach {
                it.root?.sendState = sendState
                it.root?.sendStateDetails = null
            }
            roomSummaryUpdater.updateSendingInformation(realm, roomId)
        }
    }

    fun getAllFailedEventsToResend(roomId: String): List<TimelineEvent> {
        return getAllEventsWithStates(roomId, SendState.HAS_FAILED_STATES)
    }

    fun getAllEventsWithStates(roomId: String, states: List<SendState>): List<TimelineEvent> {
        return realmSessionProvider.withRealm { realm ->
            TimelineEventEntity.findAllInRoomWithSendStates(
                    realm,
                    roomId,
                    states
            ).sortedByDescending { it.displayIndex }.mapNotNull { it?.let { timelineEventMapper.map(it) } }.filter { event ->
                when (event.root.getClearType()) {
                    EventType.MESSAGE, EventType.REDACTION, EventType.REACTION -> {
                        val content = event.root.getClearContent().toModel<MessageContent>()
                        if (content != null) {
                            when (content.msgType) {
                                MessageType.MSGTYPE_GIFTS, MessageType.MSGTYPE_RED_PACKET, MessageType.MSGTYPE_EMOTE, MessageType.MSGTYPE_NOTICE, MessageType.MSGTYPE_LOCATION, MessageType.MSGTYPE_TEXT, MessageType.MSGTYPE_FILE, MessageType.MSGTYPE_VIDEO, MessageType.MSGTYPE_IMAGE, MessageType.MSGTYPE_AUDIO -> {
                                    
                                    true
                                }
                                else                                                                                                                                                                                                                                                                                -> {
                                    Timber.e("Cannot resend message ${event.root.getClearType()} / ${content.msgType}")
                                    false
                                }
                            }
                        } else {
                            Timber.e("Unsupported message to resend ${event.root.getClearType()}")
                            false
                        }
                    }
                    else                                                       -> {
                        Timber.e("Unsupported message to resend ${event.root.getClearType()}")
                        false
                    }
                }
            }
        }
    }

    
    fun getLatestThreadEvent(rootThreadEventId: String): String {
        return realmSessionProvider.withRealm { realm ->
            EventEntity.where(realm, eventId = rootThreadEventId).findFirst()?.threadSummaryLatestMessage?.eventId
        } ?: rootThreadEventId
    }
}
