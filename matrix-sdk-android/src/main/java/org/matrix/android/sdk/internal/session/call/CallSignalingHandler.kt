

package org.matrix.android.sdk.internal.session.call

import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.call.CallListener
import org.matrix.android.sdk.api.session.call.CallState
import org.matrix.android.sdk.api.session.call.MxCall
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.call.CallAnswerContent
import org.matrix.android.sdk.api.session.room.model.call.CallAssertedIdentityContent
import org.matrix.android.sdk.api.session.room.model.call.CallCandidatesContent
import org.matrix.android.sdk.api.session.room.model.call.CallHangupContent
import org.matrix.android.sdk.api.session.room.model.call.CallInviteContent
import org.matrix.android.sdk.api.session.room.model.call.CallNegotiateContent
import org.matrix.android.sdk.api.session.room.model.call.CallRejectContent
import org.matrix.android.sdk.api.session.room.model.call.CallSelectAnswerContent
import org.matrix.android.sdk.api.session.room.model.call.CallSignalingContent
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.session.SessionScope
import timber.log.Timber
import javax.inject.Inject

private val loggerTag = LoggerTag("CallSignalingHandler", LoggerTag.VOIP)
private const val MAX_AGE_TO_RING = 40_000

@SessionScope
internal class CallSignalingHandler @Inject constructor(private val activeCallHandler: ActiveCallHandler,
                                                        private val mxCallFactory: MxCallFactory,
                                                        @UserId private val userId: String) {

    private val invitedCallIds = mutableSetOf<String>()
    private val callListeners = mutableSetOf<CallListener>()
    private val callListenersDispatcher = CallListenersDispatcher(callListeners)

    fun addCallListener(listener: CallListener) {
        callListeners.add(listener)
    }

    fun removeCallListener(listener: CallListener) {
        callListeners.remove(listener)
    }

    fun onCallEvent(event: Event) {
        when (event.getClearType()) {
            EventType.CALL_ANSWER                   -> {
                handleCallAnswerEvent(event)
            }
            EventType.CALL_INVITE                   -> {
                handleCallInviteEvent(event)
            }
            EventType.CALL_HANGUP                   -> {
                handleCallHangupEvent(event)
            }
            EventType.CALL_REJECT                   -> {
                handleCallRejectEvent(event)
            }
            EventType.CALL_CANDIDATES               -> {
                handleCallCandidatesEvent(event)
            }
            EventType.CALL_SELECT_ANSWER            -> {
                handleCallSelectAnswerEvent(event)
            }
            EventType.CALL_NEGOTIATE                -> {
                handleCallNegotiateEvent(event)
            }
            EventType.CALL_ASSERTED_IDENTITY,
            EventType.CALL_ASSERTED_IDENTITY_PREFIX -> {
                handleCallAssertedIdentityEvent(event)
            }
        }
    }

    private fun handleCallAssertedIdentityEvent(event: Event) {
        val content = event.getClearContent().toModel<CallAssertedIdentityContent>() ?: return
        val call = content.getCall() ?: return
        if (call.ourPartyId == content.partyId) {
            
            return
        }
        callListenersDispatcher.onCallAssertedIdentityReceived(content)
    }

    private fun handleCallNegotiateEvent(event: Event) {
        val content = event.getClearContent().toModel<CallNegotiateContent>() ?: return
        val call = content.getCall() ?: return
        if (call.ourPartyId == content.partyId) {
            
            return
        }
        callListenersDispatcher.onCallNegotiateReceived(content)
    }

    private fun handleCallSelectAnswerEvent(event: Event) {
        val content = event.getClearContent().toModel<CallSelectAnswerContent>() ?: return
        val call = content.getCall() ?: return
        if (call.ourPartyId == content.partyId) {
            
            return
        }
        if (call.isOutgoing) {
            Timber.tag(loggerTag.value).v("Got selectAnswer for an outbound call: ignoring")
            return
        }
        val selectedPartyId = content.selectedPartyId
        if (selectedPartyId == null) {
            Timber.tag(loggerTag.value).w("Got nonsensical select_answer with null selected_party_id: ignoring")
            return
        }
        callListenersDispatcher.onCallSelectAnswerReceived(content)
    }

    private fun handleCallCandidatesEvent(event: Event) {
        val content = event.getClearContent().toModel<CallCandidatesContent>() ?: return
        val call = content.getCall() ?: return
        if (call.ourPartyId == content.partyId) {
            
            return
        }
        if (call.opponentPartyId != null && !call.partyIdsMatches(content)) {
            Timber.tag(loggerTag.value).v("Ignoring candidates from party ID ${content.partyId} we have chosen party ID ${call.opponentPartyId}")
            return
        }
        callListenersDispatcher.onCallIceCandidateReceived(call, content)
    }

    private fun handleCallRejectEvent(event: Event) {
        val content = event.getClearContent().toModel<CallRejectContent>() ?: return
        val call = content.getCall() ?: return
        if (call.ourPartyId == content.partyId) {
            
            return
        }
        activeCallHandler.removeCall(content.callId)
        if (event.senderId == userId) {
            
            callListenersDispatcher.onCallManagedByOtherSession(content.callId)
            return
        }
        
        
        if (call.state != CallState.Dialing) {
            return
        }
        callListenersDispatcher.onCallRejectReceived(content)
    }

    private fun handleCallHangupEvent(event: Event) {
        val content = event.getClearContent().toModel<CallHangupContent>() ?: return
        val call = content.getCall() ?: return
        
        
        if (call.opponentPartyId != null && !call.partyIdsMatches(content)) {
            Timber.tag(loggerTag.value).v("Ignoring hangup from party ID ${content.partyId} we have chosen party ID ${call.opponentPartyId}")
            return
        }
        if (call.state !is CallState.Ended) {
            activeCallHandler.removeCall(content.callId)
            callListenersDispatcher.onCallHangupReceived(content)
        }
    }

    private fun handleCallInviteEvent(event: Event) {
        if (event.senderId == userId) {
            
            return
        }
        if (event.roomId == null || event.senderId == null) {
            return
        }
        val now = System.currentTimeMillis()
        val age = now - (event.ageLocalTs ?: now)
        if (age > MAX_AGE_TO_RING) {
            Timber.tag(loggerTag.value).w("Call invite is too old to ring.")
            return
        }
        val content = event.getClearContent().toModel<CallInviteContent>() ?: return

        content.callId ?: return
        if (invitedCallIds.contains(content.callId)) {
            
            Timber.tag(loggerTag.value).d("Ignoring already known call invite")
            return
        }
        val incomingCall = mxCallFactory.createIncomingCall(
                roomId = event.roomId,
                opponentUserId = event.senderId,
                content = content
        ) ?: return
        invitedCallIds.add(content.callId)
        activeCallHandler.addCall(incomingCall)
        callListenersDispatcher.onCallInviteReceived(incomingCall, content)
    }

    private fun handleCallAnswerEvent(event: Event) {
        val content = event.getClearContent().toModel<CallAnswerContent>() ?: return
        val call = content.getCall() ?: return
        if (call.ourPartyId == content.partyId) {
            
            return
        }
        if (event.roomId == null || event.senderId == null) {
            return
        }
        if (event.senderId == userId) {
            
            activeCallHandler.removeCall(call.callId)
            callListenersDispatcher.onCallManagedByOtherSession(content.callId)
        } else {
            if (call.opponentPartyId != null) {
                Timber.tag(loggerTag.value)
                        .v("Ignoring answer from party ID ${content.partyId} we already have an answer from ${call.opponentPartyId}")
                return
            }
            mxCallFactory.updateOutgoingCallWithOpponentData(call, event.senderId, content, content.capabilities)
            callListenersDispatcher.onCallAnswerReceived(content)
        }
    }

    private fun MxCall.partyIdsMatches(contentSignalingContent: CallSignalingContent): Boolean {
        return opponentPartyId?.getOrNull() == contentSignalingContent.partyId
    }

    private fun CallSignalingContent.getCall(): MxCall? {
        val currentCall = callId?.let {
            activeCallHandler.getCallWithId(it)
        }
        if (currentCall == null) {
            Timber.tag(loggerTag.value).v("Call with id $callId is null")
        }
        return currentCall
    }
}
