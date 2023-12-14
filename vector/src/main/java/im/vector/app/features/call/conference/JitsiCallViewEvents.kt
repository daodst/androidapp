

package im.vector.app.features.call.conference

import im.vector.app.core.platform.VectorViewEvents
import org.jitsi.meet.sdk.JitsiMeetUserInfo

sealed class JitsiCallViewEvents : VectorViewEvents {
    data class JoinConference(
            val enableVideo: Boolean,
            val jitsiUrl: String,
            val subject: String,
            val confId: String,
            val userInfo: JitsiMeetUserInfo,
            val token: String?
    ) : JitsiCallViewEvents()

    data class ConfirmSwitchingConference(
            val args: VectorJitsiActivity.Args
    ) : JitsiCallViewEvents()

    object LeaveConference : JitsiCallViewEvents()
    object FailJoiningConference : JitsiCallViewEvents()
    object Finish : JitsiCallViewEvents()
}
