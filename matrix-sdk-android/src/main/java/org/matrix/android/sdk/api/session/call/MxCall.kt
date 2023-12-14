

package org.matrix.android.sdk.api.session.call

import android.text.TextUtils
import org.matrix.android.sdk.api.session.room.model.call.CallCandidate
import org.matrix.android.sdk.api.session.room.model.call.CallCapabilities
import org.matrix.android.sdk.api.session.room.model.call.EndCallReason
import org.matrix.android.sdk.api.session.room.model.call.SdpType
import org.matrix.android.sdk.api.util.Optional

interface MxCallDetail {
    val callId: String
    val isOutgoing: Boolean
    val roomId: String
    val isVideoCall: Boolean
    val ourPartyId: String
    val opponentPartyId: Optional<String>?
    val opponentVersion: Int
    val opponentUserId: String
    val capabilities: CallCapabilities?
}

fun getAddressByUid(userId: String): String {
    if (TextUtils.isEmpty(userId)) {
        return userId
    }
    var address = userId
    if (address.startsWith("@")) {
        address = address.substring(1)
    }
    if (address.contains(":")) {
        val strs = address.split(":").toTypedArray()
        address = strs[0]
    }
    return address
}

fun getServerNoticeId(userId: String): String {
    return "!_server${getAddressByUid(userId)}:${getServerNameByUid(userId)}"
}

fun izServerNoticeId(roomId: String, userId: String): Boolean {
    return roomId == getServerNoticeId(userId)
}

fun getServerNameByUid(userId: String): String {
    if (TextUtils.isEmpty(userId)) {
        return userId
    }
    var address = userId
    if (address.startsWith("@")) {
        address = address.substring(1)
    }
    if (address.contains(":")) {
        val strs = address.split(":").toTypedArray()
        address = strs[1]
    }
    return address
}

const val MXCALL_KEY_PHONE = "phone"


interface MxCall : MxCallDetail {

    companion object {
        const val VOIP_PROTO_VERSION = 1
    }

    var state: CallState

    
    fun accept(sdpString: String)

    
    fun negotiate(sdpString: String, type: SdpType)

    
    fun selectAnswer()

    
    fun reject()

    
    fun hangUp(reason: EndCallReason? = null)

    
    fun offerSdp(sdpString: String)

    
    fun sendLocalCallCandidates(candidates: List<CallCandidate>)

    
    fun sendLocalIceCandidateRemovals(candidates: List<CallCandidate>)

    
    suspend fun transfer(targetUserId: String,
                         targetRoomId: String?,
                         createCallId: String?,
                         awaitCallId: String?)

    fun addListener(listener: StateListener)
    fun removeListener(listener: StateListener)

    interface StateListener {
        fun onStateUpdate(call: MxCall)
    }
}
