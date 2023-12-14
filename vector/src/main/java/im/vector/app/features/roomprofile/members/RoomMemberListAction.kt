

package im.vector.app.features.roomprofile.members

import im.vector.app.core.platform.VectorViewModelAction

sealed class RoomMemberListAction : VectorViewModelAction {
    data class RevokeThreePidInvite(val stateKey: String) : RoomMemberListAction()
    data class FilterMemberList(val searchTerm: String) : RoomMemberListAction()
}
