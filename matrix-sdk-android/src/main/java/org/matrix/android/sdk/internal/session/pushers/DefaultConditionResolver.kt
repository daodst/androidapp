
package org.matrix.android.sdk.internal.session.pushers

import org.matrix.android.sdk.api.pushrules.ConditionResolver
import org.matrix.android.sdk.api.pushrules.ContainsDisplayNameCondition
import org.matrix.android.sdk.api.pushrules.EventMatchCondition
import org.matrix.android.sdk.api.pushrules.RoomMemberCountCondition
import org.matrix.android.sdk.api.pushrules.SenderNotificationPermissionCondition
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.session.room.RoomGetter
import javax.inject.Inject

internal class DefaultConditionResolver @Inject constructor(
        private val roomGetter: RoomGetter,
        @UserId private val userId: String
) : ConditionResolver {

    override fun resolveEventMatchCondition(event: Event,
                                            condition: EventMatchCondition): Boolean {
        return condition.isSatisfied(event)
    }

    override fun resolveRoomMemberCountCondition(event: Event,
                                                 condition: RoomMemberCountCondition): Boolean {
        return condition.isSatisfied(event, roomGetter)
    }

    override fun resolveSenderNotificationPermissionCondition(event: Event,
                                                              condition: SenderNotificationPermissionCondition): Boolean {
        val roomId = event.roomId ?: return false
        val room = roomGetter.getRoom(roomId) ?: return false

        val powerLevelsContent = room.getStateEvent(EventType.STATE_ROOM_POWER_LEVELS)
                ?.content
                ?.toModel<PowerLevelsContent>()
                ?: PowerLevelsContent()

        return condition.isSatisfied(event, powerLevelsContent)
    }

    override fun resolveContainsDisplayNameCondition(event: Event,
                                                     condition: ContainsDisplayNameCondition): Boolean {
        val roomId = event.roomId ?: return false
        val room = roomGetter.getRoom(roomId) ?: return false
        val myDisplayName = room.getRoomMember(userId)?.displayName ?: return false
        return condition.isSatisfied(event, myDisplayName)
    }
}
