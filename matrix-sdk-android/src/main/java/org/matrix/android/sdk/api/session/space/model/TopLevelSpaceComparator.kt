

package org.matrix.android.sdk.api.session.space.model

import org.matrix.android.sdk.api.session.room.model.RoomSummary

class TopLevelSpaceComparator(val orders: Map<String, String?>) : Comparator<RoomSummary> {

    override fun compare(left: RoomSummary?, right: RoomSummary?): Int {
        val leftOrder = left?.roomId?.let { orders[it] }
        val rightOrder = right?.roomId?.let { orders[it] }
        return if (leftOrder != null && rightOrder != null) {
            leftOrder.compareTo(rightOrder)
        } else {
            if (leftOrder == null) {
                if (rightOrder == null) {
                    compareValues(left?.roomId, right?.roomId)
                } else {
                    1
                }
            } else {
                -1
            }
        }
    }
}
