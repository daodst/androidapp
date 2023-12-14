

package im.vector.app.features.home.room.detail.timeline.factory

import im.vector.app.features.home.room.detail.timeline.TimelineEventController
import im.vector.app.features.home.room.detail.timeline.helper.TimelineEventsGroup
import im.vector.app.features.home.room.detail.timeline.item.ReactionsSummaryEvents
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

data class TimelineItemFactoryParams(
        val izGroup: Boolean = false,
        val izOwner: Boolean = false,
        val event: TimelineEvent,
        val prevEvent: TimelineEvent? = null,
        val prevDisplayableEvent: TimelineEvent? = null,
        val nextEvent: TimelineEvent? = null,
        val nextDisplayableEvent: TimelineEvent? = null,
        val partialState: TimelineEventController.PartialState = TimelineEventController.PartialState(),
        val lastSentEventIdWithoutReadReceipts: String? = null,
        val callback: TimelineEventController.Callback? = null,
        val reactionsSummaryEvents: ReactionsSummaryEvents? = null,
        val eventsGroup: TimelineEventsGroup? = null
) {

    val highlightedEventId: String?
        get() = partialState.highlightedEventId

    val rootThreadEventId: String?
        get() = partialState.rootThreadEventId

    val isHighlighted = highlightedEventId == event.eventId

    fun isFromThreadTimeline(): Boolean = rootThreadEventId != null
}
