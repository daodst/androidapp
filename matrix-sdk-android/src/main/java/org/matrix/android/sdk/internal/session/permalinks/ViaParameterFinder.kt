

package org.matrix.android.sdk.internal.session.permalinks

import org.matrix.android.sdk.api.MatrixPatterns.getDomain
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.session.room.RoomGetter
import org.matrix.android.sdk.internal.session.room.state.StateEventDataSource
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Provider

internal class ViaParameterFinder @Inject constructor(
        @UserId private val userId: String,
        private val roomGetterProvider: Provider<RoomGetter>,
        private val stateEventDataSource: StateEventDataSource
) {

    fun computeViaParams(roomId: String, max: Int): List<String> {
        return computeViaParams(userId, roomId, max)
    }

    
    fun computeViaParams(userId: String, roomId: String): String {
        return asUrlViaParameters(computeViaParams(userId, roomId, 3))
    }

    fun asUrlViaParameters(viaList: List<String>): String {
        return viaList.joinToString(prefix = "?via=", separator = "&via=") { URLEncoder.encode(it, "utf-8") }
    }

    fun computeViaParams(userId: String, roomId: String, max: Int): List<String> {
        val userHomeserver = userId.getDomain()
        return getUserIdsOfJoinedMembers(roomId)
                .map { it.getDomain() }
                .groupBy { it }
                .mapValues { it.value.size }
                .toMutableMap()
                
                .apply { this[userHomeserver] = Int.MAX_VALUE }
                .let { map -> map.keys.sortedByDescending { map[it] } }
                .take(max)
    }

    
    private fun getUserIdsOfJoinedMembers(roomId: String): Set<String> {
        return roomGetterProvider.get().getRoom(roomId)
                ?.getRoomMembers(roomMemberQueryParams { memberships = listOf(Membership.JOIN) })
                ?.map { it.userId }
                .orEmpty()
                .toSet()
    }

    
    
    
    
    fun computeViaParamsForRestricted(roomId: String, max: Int): List<String> {
        val userThatCanInvite = roomGetterProvider.get().getRoom(roomId)
                ?.getRoomMembers(roomMemberQueryParams { memberships = listOf(Membership.JOIN) })
                ?.map { it.userId }
                ?.filter { userCanInvite(userId, roomId) }
                .orEmpty()
                .toSet()

        return userThatCanInvite.map { it.getDomain() }
                .groupBy { it }
                .mapValues { it.value.size }
                .toMutableMap()
                .let { map -> map.keys.sortedByDescending { map[it] } }
                .take(max)
    }

    fun userCanInvite(userId: String, roomId: String): Boolean {
        val powerLevelsHelper = stateEventDataSource.getStateEvent(roomId, EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.NoCondition)
                ?.content?.toModel<PowerLevelsContent>()
                ?.let { PowerLevelsHelper(it) }

        return powerLevelsHelper?.isUserAbleToInvite(userId) ?: false
    }
}
