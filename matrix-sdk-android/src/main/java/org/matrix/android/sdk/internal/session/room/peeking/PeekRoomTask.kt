

package org.matrix.android.sdk.internal.session.room.peeking

import org.matrix.android.sdk.api.MatrixPatterns
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.RoomAvatarContent
import org.matrix.android.sdk.api.session.room.model.RoomCanonicalAliasContent
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.RoomHistoryVisibility
import org.matrix.android.sdk.api.session.room.model.RoomHistoryVisibilityContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.api.session.room.model.RoomNameContent
import org.matrix.android.sdk.api.session.room.model.RoomTopicContent
import org.matrix.android.sdk.api.session.room.model.create.RoomCreateContent
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoomsFilter
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoomsParams
import org.matrix.android.sdk.api.session.room.peeking.PeekResult
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.internal.session.room.GetRoomSummaryTask
import org.matrix.android.sdk.internal.session.room.alias.GetRoomIdByAliasTask
import org.matrix.android.sdk.internal.session.room.directory.GetPublicRoomTask
import org.matrix.android.sdk.internal.session.room.directory.GetRoomDirectoryVisibilityTask
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface PeekRoomTask : Task<PeekRoomTask.Params, PeekResult> {
    data class Params(
            val roomIdOrAlias: String
    )
}

internal class DefaultPeekRoomTask @Inject constructor(
        private val getRoomIdByAliasTask: GetRoomIdByAliasTask,
        private val getRoomDirectoryVisibilityTask: GetRoomDirectoryVisibilityTask,
        private val getPublicRoomTask: GetPublicRoomTask,
        private val getRoomSummaryTask: GetRoomSummaryTask,
        private val resolveRoomStateTask: ResolveRoomStateTask
) : PeekRoomTask {

    override suspend fun execute(params: PeekRoomTask.Params): PeekResult {
        val roomId: String
        val serverList: List<String>
        val isAlias = MatrixPatterns.isRoomAlias(params.roomIdOrAlias)
        if (isAlias) {
            
            val aliasDescription = getRoomIdByAliasTask
                    .execute(GetRoomIdByAliasTask.Params(params.roomIdOrAlias, true))
                    .getOrNull()
                    ?: return PeekResult.UnknownAlias

            roomId = aliasDescription.roomId
            serverList = aliasDescription.servers
        } else {
            roomId = params.roomIdOrAlias
            serverList = emptyList()
        }

        
        val strippedState = tryOrNull("Failed to get room stripped state roomId:$roomId") {
            getRoomSummaryTask.execute(GetRoomSummaryTask.Params(roomId, serverList))
        }
        if (strippedState != null) {
            return PeekResult.Success(
                    roomId = strippedState.roomId,
                    alias = strippedState.getPrimaryAlias() ?: params.roomIdOrAlias.takeIf { isAlias },
                    avatarUrl = strippedState.avatarUrl,
                    name = strippedState.name,
                    topic = strippedState.topic,
                    numJoinedMembers = strippedState.numJoinedMembers,
                    viaServers = serverList,
                    roomType = strippedState.roomType,
                    someMembers = null,
                    isPublic = strippedState.worldReadable
            )
        }

        
        val visibilityRes = tryOrNull("## PEEK: failed to get visibility") {
            getRoomDirectoryVisibilityTask.execute(GetRoomDirectoryVisibilityTask.Params(roomId))
        }
        val publicRepoResult = when (visibilityRes) {
            RoomDirectoryVisibility.PUBLIC -> {
                
                val filter = if (isAlias) PublicRoomsFilter(searchTerm = params.roomIdOrAlias.substring(1))
                else null

                tryOrNull("## PEEK: failed to GetPublicRoomTask") {
                    getPublicRoomTask.execute(GetPublicRoomTask.Params(
                            server = serverList.firstOrNull(),
                            publicRoomsParams = PublicRoomsParams(
                                    filter = filter,
                                    limit = 20.takeIf { filter != null } ?: 100
                            )
                    ))
                }?.chunk?.firstOrNull { it.roomId == roomId }
            }
            else                           -> {
                
                
                null
            }
        }

        if (publicRepoResult != null) {
            return PeekResult.Success(
                    roomId = roomId,
                    alias = publicRepoResult.getPrimaryAlias() ?: params.roomIdOrAlias.takeIf { isAlias },
                    avatarUrl = publicRepoResult.avatarUrl,
                    name = publicRepoResult.name,
                    topic = publicRepoResult.topic,
                    numJoinedMembers = publicRepoResult.numJoinedMembers,
                    viaServers = serverList,
                    roomType = null, 
                    someMembers = null,
                    isPublic = true
            )
        }

        
        
        try {
            val stateEvents = resolveRoomStateTask.execute(ResolveRoomStateTask.Params(roomId))
            val name = stateEvents
                    .lastOrNull { it.type == EventType.STATE_ROOM_NAME && it.stateKey == "" }
                    ?.let { it.content?.toModel<RoomNameContent>()?.name }

            val topic = stateEvents
                    .lastOrNull { it.type == EventType.STATE_ROOM_TOPIC && it.stateKey == "" }
                    ?.let { it.content?.toModel<RoomTopicContent>()?.topic }

            val avatarUrl = stateEvents
                    .lastOrNull { it.type == EventType.STATE_ROOM_AVATAR }
                    ?.let { it.content?.toModel<RoomAvatarContent>()?.avatarUrl }

            val alias = stateEvents
                    .lastOrNull { it.type == EventType.STATE_ROOM_CANONICAL_ALIAS }
                    ?.let { it.content?.toModel<RoomCanonicalAliasContent>()?.canonicalAlias }

            
            val membersEvent = stateEvents
                    .filter { it.type == EventType.STATE_ROOM_MEMBER && it.stateKey?.isNotEmpty() == true }

            val memberCount = membersEvent
                    .distinctBy { it.stateKey }
                    .count()

            val someMembers = membersEvent.mapNotNull { ev ->
                ev.content?.toModel<RoomMemberContent>()?.let {
                    MatrixItem.UserItem(ev.stateKey ?: "", it.displayName, it.avatarUrl)
                }
            }

            val historyVisibility =
                    stateEvents
                            .lastOrNull { it.type == EventType.STATE_ROOM_HISTORY_VISIBILITY && it.stateKey?.isNotEmpty() == true }
                            ?.let { it.content?.toModel<RoomHistoryVisibilityContent>()?.historyVisibility }

            val roomType = stateEvents
                    .lastOrNull { it.type == EventType.STATE_ROOM_CREATE }
                    ?.content
                    ?.toModel<RoomCreateContent>()
                    ?.type

            return PeekResult.Success(
                    roomId = roomId,
                    alias = alias,
                    avatarUrl = avatarUrl,
                    name = name,
                    topic = topic,
                    numJoinedMembers = memberCount,
                    roomType = roomType,
                    viaServers = serverList,
                    someMembers = someMembers,
                    isPublic = historyVisibility == RoomHistoryVisibility.WORLD_READABLE
            )
        } catch (failure: Throwable) {
            
            
            return PeekResult.PeekingNotAllowed(
                    roomId = roomId,
                    alias = params.roomIdOrAlias.takeIf { isAlias },
                    viaServers = serverList
            )
        }
    }
}
