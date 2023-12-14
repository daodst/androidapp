

package im.vector.app.features.call.transfer

import com.airbnb.mvrx.MavericksState

data class CallTransferViewState(
        val callId: String
) : MavericksState {

    constructor(args: CallTransferArgs) : this(callId = args.callId)
}
