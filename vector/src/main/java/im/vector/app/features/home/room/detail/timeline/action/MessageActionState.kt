

package im.vector.app.features.home.room.detail.timeline.action

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.core.extensions.canReact
import im.vector.app.features.home.room.detail.timeline.item.MessageInformationData
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent


data class ToggleState(
        val reaction: String,
        val isSelected: Boolean
)

data class ActionPermissions(
        val canSendMessage: Boolean = false,
        val canReact: Boolean = false,
        val canRedact: Boolean = false
)

data class MessageActionState(
        val roomId: String,
        val eventId: String,
        val informationData: MessageInformationData,
        val timelineEvent: Async<TimelineEvent> = Uninitialized,
        val messageBody: CharSequence = "",
        
        val quickStates: Async<List<ToggleState>> = Uninitialized,
        
        val actions: List<EventSharedAction> = emptyList(),
        val expendedReportContentMenu: Boolean = false,
        val actionPermissions: ActionPermissions = ActionPermissions(),
        val isFromThreadTimeline: Boolean = false
) : MavericksState {

    constructor(args: TimelineEventFragmentArgs) : this(
            roomId = args.roomId,
            eventId = args.eventId,
            informationData = args.informationData,
            isFromThreadTimeline = args.isFromThreadTimeline)

    fun senderName(): String = informationData.memberName?.toString() ?: ""

    fun canReact() = timelineEvent()?.canReact() == true && actionPermissions.canReact

    fun sendState(): SendState? = timelineEvent()?.root?.sendState
}
