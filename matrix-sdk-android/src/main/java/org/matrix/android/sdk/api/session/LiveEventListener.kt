

package org.matrix.android.sdk.api.session

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.util.JsonDict

interface LiveEventListener {

    fun onLiveEvent(roomId: String, event: Event)

    fun onPaginatedEvent(roomId: String, event: Event)

    fun onEventDecrypted(event: Event, clearEvent: JsonDict)

    fun onEventDecryptionError(event: Event, throwable: Throwable)

    fun onLiveToDeviceEvent(event: Event)

    
}
