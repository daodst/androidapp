

package org.matrix.android.sdk.api.session.call

interface CallSignalingService {

    suspend fun getTurnServer(): TurnServerResponse

    
    fun createOutgoingCall(roomId: String, otherUserId: String, isVideoCall: Boolean): MxCall

    fun addCallListener(listener: CallListener)

    fun removeCallListener(listener: CallListener)

    fun getCallWithId(callId: String): MxCall?

    fun isThereAnyActiveCall(): Boolean
}
