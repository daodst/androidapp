

package im.vector.app.features.invite

import androidx.core.view.isGone
import im.vector.app.core.platform.ButtonStateView
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState

object InviteButtonStateBinder {

    fun bind(
            acceptView: ButtonStateView,
            rejectView: ButtonStateView,
            changeMembershipState: ChangeMembershipState
    ) {
        
        

        val requestInProgress = changeMembershipState.isInProgress() || changeMembershipState.isSuccessful()
        when {
            requestInProgress                                            -> acceptView.render(ButtonStateView.State.Loading)
            changeMembershipState is ChangeMembershipState.FailedJoining -> acceptView.render(ButtonStateView.State.Error)
            else                                                         -> acceptView.render(ButtonStateView.State.Button)
        }
        

        rejectView.isGone = requestInProgress

        when (changeMembershipState) {
            is ChangeMembershipState.FailedLeaving -> rejectView.render(ButtonStateView.State.Error)
            else                                   -> rejectView.render(ButtonStateView.State.Button)
        }
    }
}
