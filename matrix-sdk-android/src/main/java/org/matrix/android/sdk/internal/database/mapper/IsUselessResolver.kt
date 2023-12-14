

package org.matrix.android.sdk.internal.database.mapper

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toContent

internal object IsUselessResolver {

    
    fun isUseless(event: Event): Boolean {
        return when (event.type) {
            EventType.STATE_ROOM_MEMBER -> {
                
                event.content != null &&
                        event.content.toContent() == event.resolvedPrevContent()?.toContent()
            }
            else                        -> false
        }
    }
}
