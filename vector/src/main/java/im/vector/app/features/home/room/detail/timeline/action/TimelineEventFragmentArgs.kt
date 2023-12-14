

package im.vector.app.features.home.room.detail.timeline.action

import android.os.Parcelable
import im.vector.app.features.home.room.detail.timeline.item.MessageInformationData
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimelineEventFragmentArgs(
        val eventId: String,
        val roomId: String,
        val informationData: MessageInformationData,
        val isFromThreadTimeline: Boolean = false
) : Parcelable
