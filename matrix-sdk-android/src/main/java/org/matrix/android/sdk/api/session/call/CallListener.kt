

package org.matrix.android.sdk.api.session.call

import org.matrix.android.sdk.api.session.room.model.call.CallAnswerContent
import org.matrix.android.sdk.api.session.room.model.call.CallAssertedIdentityContent
import org.matrix.android.sdk.api.session.room.model.call.CallCandidatesContent
import org.matrix.android.sdk.api.session.room.model.call.CallHangupContent
import org.matrix.android.sdk.api.session.room.model.call.CallInviteContent
import org.matrix.android.sdk.api.session.room.model.call.CallNegotiateContent
import org.matrix.android.sdk.api.session.room.model.call.CallRejectContent
import org.matrix.android.sdk.api.session.room.model.call.CallSelectAnswerContent

interface CallListener {
    
    fun onCallInviteReceived(mxCall: MxCall, callInviteContent: CallInviteContent)

    fun onCallIceCandidateReceived(mxCall: MxCall, iceCandidatesContent: CallCandidatesContent)

    
    fun onCallAnswerReceived(callAnswerContent: CallAnswerContent)

    
    fun onCallHangupReceived(callHangupContent: CallHangupContent)

    
    fun onCallRejectReceived(callRejectContent: CallRejectContent)

    
    fun onCallSelectAnswerReceived(callSelectAnswerContent: CallSelectAnswerContent)

    
    fun onCallNegotiateReceived(callNegotiateContent: CallNegotiateContent)

    
    fun onCallManagedByOtherSession(callId: String)

    
    fun onCallAssertedIdentityReceived(callAssertedIdentityContent: CallAssertedIdentityContent)
}
