

package org.matrix.android.sdk.internal.session.room.send

import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject


@SessionScope
internal class CancelSendTracker @Inject constructor() {

    data class Request(
            val localId: String,
            val roomId: String
    )

    private val cancellingRequests = ArrayList<Request>()

    fun markLocalEchoForCancel(eventId: String, roomId: String) {
        synchronized(cancellingRequests) {
            cancellingRequests.add(Request(eventId, roomId))
        }
    }

    fun isCancelRequestedFor(eventId: String?, roomId: String?): Boolean {
        val found = synchronized(cancellingRequests) {
            cancellingRequests.any { it.localId == eventId && it.roomId == roomId }
        }
        return found
    }

    fun markCancelled(eventId: String, roomId: String) {
        synchronized(cancellingRequests) {
            val index = cancellingRequests.indexOfFirst { it.localId == eventId && it.roomId == roomId }
            if (index != -1) {
                cancellingRequests.removeAt(index)
            }
        }
    }
}
