

package org.matrix.android.sdk.internal.session.room.timeline

import org.matrix.android.sdk.api.session.events.model.Event

internal interface TokenChunkEvent {
    val start: String?
    val end: String?
    val events: List<Event>
    val stateEvents: List<Event>?

    fun hasMore() = start != end
}
