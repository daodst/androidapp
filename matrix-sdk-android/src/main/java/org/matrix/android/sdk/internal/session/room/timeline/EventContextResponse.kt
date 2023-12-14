

package org.matrix.android.sdk.internal.session.room.timeline

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Event

@JsonClass(generateAdapter = true)
internal data class EventContextResponse(
        
        @Json(name = "event") val event: Event,
        
        @Json(name = "start") override val start: String? = null,
        
        @Json(name = "events_before") val eventsBefore: List<Event>? = null,
        
        @Json(name = "events_after") val eventsAfter: List<Event>? = null,
        
        @Json(name = "end") override val end: String? = null,
        
        @Json(name = "state") override val stateEvents: List<Event>? = null
) : TokenChunkEvent {

    override val events: List<Event> by lazy {
        eventsAfter.orEmpty().reversed() + event + eventsBefore.orEmpty()
    }
}
