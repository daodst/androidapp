

package org.matrix.android.sdk.internal.session.room.summary

import io.realm.Realm
import org.matrix.android.sdk.api.session.room.summary.RoomSummaryConstants
import org.matrix.android.sdk.api.session.room.timeline.EventTypeFilter
import org.matrix.android.sdk.api.session.room.timeline.TimelineEventFilters
import org.matrix.android.sdk.internal.database.model.TimelineEventEntity
import org.matrix.android.sdk.internal.database.query.latestEvent

internal object RoomSummaryEventsHelper {

    private val previewFilters = TimelineEventFilters(
            filterTypes = true,
            allowedTypes = RoomSummaryConstants.PREVIEWABLE_TYPES.map { EventTypeFilter(eventType = it, stateKey = null) },
            filterUseless = true,
            filterRedacted = false,
            filterEdits = true
    )

    fun getLatestPreviewableEvent(realm: Realm, roomId: String): TimelineEventEntity? {
        return TimelineEventEntity.latestEvent(
                realm = realm,
                roomId = roomId,
                includesSending = true,
                filters = previewFilters
        )
    }
}
