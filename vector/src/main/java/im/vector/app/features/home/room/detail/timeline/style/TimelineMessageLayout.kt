

package im.vector.app.features.home.room.detail.timeline.style

import android.os.Parcelable
import im.vector.app.R
import kotlinx.parcelize.Parcelize

sealed interface TimelineMessageLayout : Parcelable {

    val layoutRes: Int
    val showAvatar: Boolean
    val showDisplayName: Boolean
    val showTimestamp: Boolean
    val izGroup: Boolean
    val isCus: Boolean
    val isLocal: Boolean

    @Parcelize
    data class Default(
            override val isLocal: Boolean = false,
            override val isCus: Boolean = false,
            override val showAvatar: Boolean,
            override val showDisplayName: Boolean,
            override val showTimestamp: Boolean,
            
            override val layoutRes: Int = 0,
            override val izGroup: Boolean = false,
    ) : TimelineMessageLayout

    @Parcelize
    data class Bubble(
            override val isLocal: Boolean = false,
            override val isCus: Boolean = false,
            override val izGroup: Boolean = false,
            override val showAvatar: Boolean,
            override val showDisplayName: Boolean,
            override val showTimestamp: Boolean = true,
            val isIncoming: Boolean,
            val addTopMargin: Boolean = false,

            val type: String = "",
            val isPseudoBubble: Boolean,
            val cornersRadius: CornersRadius,
            val timestampInsideMessage: Boolean,
            val addMessageOverlay: Boolean,
            override val layoutRes: Int = if (isIncoming) {
                R.layout.item_timeline_event_bubble_incoming_base
            } else {
                R.layout.item_timeline_event_bubble_outgoing_base
            },
    ) : TimelineMessageLayout {

        @Parcelize
        data class CornersRadius(
                val topStartRadius: Float,
                val topEndRadius: Float,
                val bottomStartRadius: Float,
                val bottomEndRadius: Float,
        ) : Parcelable
    }
}
