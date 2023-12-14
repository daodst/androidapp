

package org.matrix.android.sdk.api.session.call

import org.matrix.android.sdk.api.session.room.model.call.EndCallReason

sealed class CallState {

    
    object Idle : CallState()

    
    object CreateOffer : CallState()

    
    object Dialing : CallState()

    
    object LocalRinging : CallState()

    
    object Answering : CallState()

    
    data class Connected(val iceConnectionState: MxPeerConnectionState) : CallState()

    
    data class Ended(val reason: EndCallReason? = null) : CallState()
}
