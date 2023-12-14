

package im.vector.app.features.home.room.detail.arguments

import android.os.Parcelable
import im.vector.app.features.home.RoomListDisplayMode
import im.vector.app.features.home.room.threads.arguments.ThreadTimelineArgs
import im.vector.app.features.share.SharedData
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimelineArgs(
        val roomId: String,
        val eventId: String? = null,
        val sharedData: SharedData? = null,
        val openShareSpaceForId: String? = null,
        val threadTimelineArgs: ThreadTimelineArgs? = null,
        val switchToParentSpace: Boolean = false,
        val isInviteAlreadyAccepted: Boolean = false,
        val displayMode: RoomListDisplayMode = RoomListDisplayMode.FILTERED,

        val izCreate: Boolean = false
) : Parcelable
