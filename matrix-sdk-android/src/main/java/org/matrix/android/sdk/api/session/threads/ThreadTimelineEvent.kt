

package org.matrix.android.sdk.api.session.threads

import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent


data class ThreadTimelineEvent(
        val timelineEvent: TimelineEvent,
        val isParticipating: Boolean
)
