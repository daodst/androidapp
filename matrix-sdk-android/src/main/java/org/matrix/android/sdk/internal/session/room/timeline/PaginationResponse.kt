

package org.matrix.android.sdk.internal.session.room.timeline

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Event

@JsonClass(generateAdapter = true)
internal data class PaginationResponse(
        
        @Json(name = "start") override val start: String? = null,
        
        @Json(name = "end") override val end: String? = null,
        
        @Json(name = "chunk") val chunk: List<Event>? = null,
        
        @Json(name = "state") override val stateEvents: List<Event>? = null
) : TokenChunkEvent {
    override val events: List<Event>
        get() = chunk.orEmpty()
}
