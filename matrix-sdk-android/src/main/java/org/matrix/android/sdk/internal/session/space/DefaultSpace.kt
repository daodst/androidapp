

package org.matrix.android.sdk.internal.session.space

import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.space.Space
import org.matrix.android.sdk.api.session.space.model.SpaceChildContent
import org.matrix.android.sdk.internal.session.permalinks.ViaParameterFinder
import org.matrix.android.sdk.internal.session.room.summary.RoomSummaryDataSource

internal class DefaultSpace(
        private val room: Room,
        private val spaceSummaryDataSource: RoomSummaryDataSource,
        private val viaParameterFinder: ViaParameterFinder
) : Space {

    override fun asRoom(): Room {
        return room
    }

    override val spaceId = room.roomId

    override fun spaceSummary(): RoomSummary? {
        return spaceSummaryDataSource.getSpaceSummary(room.roomId)
    }

    override suspend fun addChildren(roomId: String,
                                     viaServers: List<String>?,
                                     order: String?,
                                     suggested: Boolean?) {
        
        val bestVia = viaServers
                ?: (spaceSummaryDataSource.getRoomSummary(roomId)
                        ?.takeIf { it.joinRules == RoomJoinRules.RESTRICTED }
                        ?.let {
                            
                            
                            viaParameterFinder.computeViaParamsForRestricted(roomId, 3)
                        }
                        ?: viaParameterFinder.computeViaParams(roomId, 3))

        room.sendStateEvent(
                eventType = EventType.STATE_SPACE_CHILD,
                stateKey = roomId,
                body = SpaceChildContent(
                        via = bestVia,
                        order = order,
                        suggested = suggested
                ).toContent()
        )
    }

    override suspend fun removeChildren(roomId: String) {

        
        room.sendStateEvent(
                eventType = EventType.STATE_SPACE_CHILD,
                stateKey = roomId,
                body = SpaceChildContent(
                        order = null,
                        via = null,
                        suggested = null
                ).toContent()
        )
    }

    override fun getChildInfo(roomId: String): SpaceChildContent? {
        return room.getStateEvents(setOf(EventType.STATE_SPACE_CHILD), QueryStringValue.Equals(roomId))
                .firstOrNull()
                ?.content.toModel<SpaceChildContent>()
    }

    override suspend fun setChildrenOrder(roomId: String, order: String?) {
        val existing = room.getStateEvents(setOf(EventType.STATE_SPACE_CHILD), QueryStringValue.Equals(roomId))
                .firstOrNull()
                ?.content.toModel<SpaceChildContent>()
                ?: throw IllegalArgumentException("$roomId is not a child of this space")

        
        room.sendStateEvent(
                eventType = EventType.STATE_SPACE_CHILD,
                stateKey = roomId,
                body = SpaceChildContent(
                        order = order,
                        via = existing.via,
                        suggested = existing.suggested
                ).toContent()
        )
    }





    override suspend fun setChildrenSuggested(roomId: String, suggested: Boolean) {
        val existing = room.getStateEvents(setOf(EventType.STATE_SPACE_CHILD), QueryStringValue.Equals(roomId))
                .firstOrNull()
                ?.content.toModel<SpaceChildContent>()
                ?: throw IllegalArgumentException("$roomId is not a child of this space")

        if (existing.suggested == suggested) {
            
            return
        }
        
        room.sendStateEvent(
                eventType = EventType.STATE_SPACE_CHILD,
                stateKey = roomId,
                body = SpaceChildContent(
                        order = existing.order,
                        via = existing.via,
                        suggested = suggested
                ).toContent()
        )
    }
}
