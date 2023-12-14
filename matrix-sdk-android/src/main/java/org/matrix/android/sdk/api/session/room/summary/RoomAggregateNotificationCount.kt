

package org.matrix.android.sdk.api.session.room.summary

data class RoomAggregateNotificationCount(
        val notificationCount: Int,
        val highlightCount: Int
) {
    val totalCount = notificationCount
    val isHighlight = highlightCount > 0
}
