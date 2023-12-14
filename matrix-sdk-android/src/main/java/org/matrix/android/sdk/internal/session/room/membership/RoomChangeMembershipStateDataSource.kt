

package org.matrix.android.sdk.internal.session.room.membership

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.internal.session.SessionScope
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject


@SessionScope
internal class RoomChangeMembershipStateDataSource @Inject constructor() {

    private val mutableLiveStates = MutableLiveData<Map<String, ChangeMembershipState>>(emptyMap())
    private val states = ConcurrentHashMap<String, ChangeMembershipState>()

    
    fun setMembershipFromSync(roomId: String, membership: Membership) {
        if (states.containsKey(roomId)) {
            val newState = membership.toMembershipChangeState()
            updateState(roomId, newState)
        }
    }

    fun updateState(roomId: String, state: ChangeMembershipState) {
        states[roomId] = state
        mutableLiveStates.postValue(states.toMap())
    }

    fun getLiveStates(): LiveData<Map<String, ChangeMembershipState>> {
        return mutableLiveStates
    }

    fun getState(roomId: String): ChangeMembershipState {
        return states.getOrElse(roomId) {
            ChangeMembershipState.Unknown
        }
    }

    private fun Membership.toMembershipChangeState(): ChangeMembershipState {
        return when {
            this == Membership.JOIN -> ChangeMembershipState.Joined
            this.isLeft()           -> ChangeMembershipState.Left
            else                    -> ChangeMembershipState.Unknown
        }
    }
}
