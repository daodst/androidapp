

package org.matrix.android.sdk.internal.session.space.peeking

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.model.create.RoomCreateContent
import org.matrix.android.sdk.api.session.room.peeking.PeekResult
import org.matrix.android.sdk.api.session.space.model.SpaceChildContent
import org.matrix.android.sdk.api.session.space.peeking.ISpaceChild
import org.matrix.android.sdk.api.session.space.peeking.SpaceChildPeekResult
import org.matrix.android.sdk.api.session.space.peeking.SpacePeekResult
import org.matrix.android.sdk.api.session.space.peeking.SpacePeekSummary
import org.matrix.android.sdk.api.session.space.peeking.SpaceSubChildPeekResult
import org.matrix.android.sdk.internal.session.room.peeking.PeekRoomTask
import org.matrix.android.sdk.internal.session.room.peeking.ResolveRoomStateTask
import org.matrix.android.sdk.internal.task.Task
import timber.log.Timber
import javax.inject.Inject

internal interface PeekSpaceTask : Task<PeekSpaceTask.Params, SpacePeekResult> {
    data class Params(
            val roomIdOrAlias: String,
            
            val maxDepth: Int = 4
    )
}

internal class DefaultPeekSpaceTask @Inject constructor(
        private val peekRoomTask: PeekRoomTask,
        private val resolveRoomStateTask: ResolveRoomStateTask
) : PeekSpaceTask {

    override suspend fun execute(params: PeekSpaceTask.Params): SpacePeekResult {
        val peekResult = peekRoomTask.execute(PeekRoomTask.Params(params.roomIdOrAlias))
        val roomResult = peekResult as? PeekResult.Success ?: return SpacePeekResult.FailedToResolve(params.roomIdOrAlias, peekResult)

        
        
        val stateEvents = try {
            resolveRoomStateTask.execute(ResolveRoomStateTask.Params(roomResult.roomId))
        } catch (failure: Throwable) {
            return SpacePeekResult.FailedToResolve(params.roomIdOrAlias, peekResult)
        }
        val isSpace = stateEvents
                .lastOrNull { it.type == EventType.STATE_ROOM_CREATE && it.stateKey == "" }
                ?.content
                ?.toModel<RoomCreateContent>()
                ?.type == RoomType.SPACE

        if (!isSpace) return SpacePeekResult.NotSpaceType(params.roomIdOrAlias)

        val children = peekChildren(stateEvents, 0, params.maxDepth)

        return SpacePeekResult.Success(
                SpacePeekSummary(
                        params.roomIdOrAlias,
                        peekResult,
                        children
                )
        )
    }

    private suspend fun peekChildren(stateEvents: List<Event>, depth: Int, maxDepth: Int): List<ISpaceChild> {
        if (depth >= maxDepth) return emptyList()
        val childRoomsIds = stateEvents
                .filter {
                    it.type == EventType.STATE_SPACE_CHILD && !it.stateKey.isNullOrEmpty() &&
                            
                            it.content?.toModel<SpaceChildContent>()?.via != null
                }
                .map { it.stateKey to it.content?.toModel<SpaceChildContent>() }

        Timber.v("## SPACE_PEEK: found ${childRoomsIds.size} present children")

        val spaceChildResults = mutableListOf<ISpaceChild>()
        childRoomsIds.forEach { entry ->

            Timber.v("## SPACE_PEEK: peeking child $entry")
            
            val childId = entry.first ?: return@forEach
            try {
                val childPeek = peekRoomTask.execute(PeekRoomTask.Params(childId))

                val childStateEvents = resolveRoomStateTask.execute(ResolveRoomStateTask.Params(childId))
                val createContent = childStateEvents
                        .lastOrNull { it.type == EventType.STATE_ROOM_CREATE && it.stateKey == "" }
                        ?.let { it.content?.toModel<RoomCreateContent>() }

                if (!childPeek.isSuccess() || createContent == null) {
                    Timber.v("## SPACE_PEEK: cannot peek child $entry")
                    
                    spaceChildResults.add(
                            SpaceChildPeekResult(
                                    childId, childPeek, entry.second?.order
                            )
                    )
                    
                    return@forEach
                }
                val type = createContent.type
                if (type == RoomType.SPACE) {
                    Timber.v("## SPACE_PEEK: subspace child $entry")
                    spaceChildResults.add(
                            SpaceSubChildPeekResult(
                                    childId,
                                    childPeek,
                                    entry.second?.order,
                                    peekChildren(childStateEvents, depth + 1, maxDepth)
                            )
                    )
                } else
                
                {
                    Timber.v("## SPACE_PEEK: room child $entry")
                    spaceChildResults.add(
                            SpaceChildPeekResult(
                                    childId, childPeek, entry.second?.order
                            )
                    )
                }

                
            } catch (failure: Throwable) {
                
                Timber.e(failure, "## Failed to resolve space child")
            }
        }
        return spaceChildResults
    }
}
