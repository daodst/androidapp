
package org.matrix.android.sdk.api.pushrules

import org.matrix.android.sdk.api.pushrules.rest.PushRule
import org.matrix.android.sdk.api.session.events.model.Event

data class PushEvents(
        val matchedEvents: List<Pair<Event, PushRule>>,
        val roomsJoined: Collection<String>,
        val roomsLeft: Collection<String>,
        val redactedEventIds: List<String>
)
