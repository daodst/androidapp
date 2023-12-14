

package im.vector.app.core.extensions

import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

fun TimelineEvent.canReact(): Boolean {
    
    return root.getClearType() in listOf(EventType.MESSAGE, EventType.STICKER) + EventType.POLL_START &&
            root.sendState == SendState.SYNCED &&
            !root.isRedacted()
}
