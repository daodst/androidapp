

package im.vector.app.features.roomprofile.daily

import im.vector.app.core.platform.VectorViewModelAction

sealed class RoomMemberDailyListAction : VectorViewModelAction {
    data class RevokeThreePidInvite(val stateKey: String) : RoomMemberDailyListAction()
    data class FilterMemberList(val searchTerm: String) : RoomMemberDailyListAction()
}
