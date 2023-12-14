

package org.matrix.android.sdk.api.session.room.members

sealed class ChangeMembershipState {
    object Unknown : ChangeMembershipState()
    object Joining : ChangeMembershipState()
    data class FailedJoining(val throwable: Throwable) : ChangeMembershipState()
    object Joined : ChangeMembershipState()
    object Leaving : ChangeMembershipState()
    data class FailedLeaving(val throwable: Throwable) : ChangeMembershipState()
    object Left : ChangeMembershipState()

    fun isInProgress() = this is Joining || this is Leaving

    fun isSuccessful() = this is Joined || this is Left

    fun isFailed() = this is FailedJoining || this is FailedLeaving
}
