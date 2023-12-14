

package org.matrix.android.sdk.api.session.events

import org.matrix.android.sdk.api.session.events.model.Event

interface EventService {

    
    suspend fun getEvent(roomId: String,
                         eventId: String): Event
}
