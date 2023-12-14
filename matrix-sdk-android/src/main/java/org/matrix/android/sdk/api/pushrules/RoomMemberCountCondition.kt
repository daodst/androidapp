
package org.matrix.android.sdk.api.pushrules

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.internal.session.room.RoomGetter
import timber.log.Timber

private val regex = Regex("^(==|<=|>=|<|>)?(\\d*)$")

class RoomMemberCountCondition(
        
        val iz: String
) : Condition {

    override fun isSatisfied(event: Event, conditionResolver: ConditionResolver): Boolean {
        return conditionResolver.resolveRoomMemberCountCondition(event, this)
    }

    override fun technicalDescription() = "Room member count is $iz"

    internal fun isSatisfied(event: Event, roomGetter: RoomGetter): Boolean {
        
        val roomId = event.roomId ?: return false
        val room = roomGetter.getRoom(roomId) ?: return false

        
        val (prefix, count) = parseIsField() ?: return false

        val numMembers = room.getNumberOfJoinedMembers()

        return when (prefix) {
            "<"  -> numMembers < count
            ">"  -> numMembers > count
            "<=" -> numMembers <= count
            ">=" -> numMembers >= count
            else -> numMembers == count
        }
    }

    
    private fun parseIsField(): Pair<String?, Int>? {
        try {
            val match = regex.find(iz) ?: return null
            val (prefix, count) = match.destructured
            return prefix to count.toInt()
        } catch (t: Throwable) {
            Timber.e(t, "Unable to parse 'is' field")
        }
        return null
    }
}
