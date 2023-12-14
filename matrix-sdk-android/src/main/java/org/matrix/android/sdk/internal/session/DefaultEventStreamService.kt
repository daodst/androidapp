

package org.matrix.android.sdk.internal.session

import org.matrix.android.sdk.api.session.EventStreamService
import org.matrix.android.sdk.api.session.LiveEventListener
import javax.inject.Inject

internal class DefaultEventStreamService @Inject constructor(
        private val streamEventsManager: StreamEventsManager
) : EventStreamService {

    override fun addEventStreamListener(streamListener: LiveEventListener) {
        streamEventsManager.addLiveEventListener(streamListener)
    }

    override fun removeEventStreamListener(streamListener: LiveEventListener) {
        streamEventsManager.removeLiveEventListener(streamListener)
    }
}
