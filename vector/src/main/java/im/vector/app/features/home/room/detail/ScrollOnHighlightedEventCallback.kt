

package im.vector.app.features.home.room.detail

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.vector.app.core.platform.DefaultListUpdateCallback
import im.vector.app.features.home.room.detail.timeline.TimelineEventController
import java.util.concurrent.atomic.AtomicReference


class ScrollOnHighlightedEventCallback(private val recyclerView: RecyclerView,
                                       private val layoutManager: LinearLayoutManager,
                                       private val timelineEventController: TimelineEventController) : DefaultListUpdateCallback {

    private val scheduledEventId = AtomicReference<String?>()

    override fun onInserted(position: Int, count: Int) {
        scrollIfNeeded()
    }

    override fun onChanged(position: Int, count: Int, tag: Any?) {
        scrollIfNeeded()
    }

    private fun scrollIfNeeded() {
        val eventId = scheduledEventId.get() ?: return
        val positionToScroll = timelineEventController.searchPositionOfEvent(eventId) ?: return
        recyclerView.stopScroll()
        layoutManager.scrollToPosition(positionToScroll)
        scheduledEventId.set(null)
    }

    fun scheduleScrollTo(eventId: String?) {
        scheduledEventId.set(eventId)
    }
}
