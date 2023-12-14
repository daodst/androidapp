

package im.vector.app.features.pay4invite

import com.airbnb.mvrx.MavericksState
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone

data class Pay4InviteViewState(
        val displayMode: Pay4InviteDisplayMode,
        val userByPhones: List<UserByPhone>?,
) : MavericksState {
    constructor(args: Pay4InviteArgs) : this(displayMode = args.displayMode, userByPhones = args.userByPhones)
}

