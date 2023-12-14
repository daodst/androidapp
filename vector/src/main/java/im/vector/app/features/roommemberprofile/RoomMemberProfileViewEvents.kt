

package im.vector.app.features.roommemberprofile

import im.vector.app.core.platform.VectorViewEvents


sealed class RoomMemberProfileViewEvents : VectorViewEvents {
    data class Loading(val message: CharSequence? = null) : RoomMemberProfileViewEvents()
    data class Failure(val throwable: Throwable) : RoomMemberProfileViewEvents()

    object OnIgnoreActionSuccess : RoomMemberProfileViewEvents()
    object OnSetPowerLevelSuccess : RoomMemberProfileViewEvents()
    object OnInviteActionSuccess : RoomMemberProfileViewEvents()
    object OnKickActionSuccess : RoomMemberProfileViewEvents()
    object OnBanActionSuccess : RoomMemberProfileViewEvents()
    object SaveRemarkSuccess : RoomMemberProfileViewEvents()
    data class ShowPowerLevelValidation(val currentValue: Int, val newValue: Int) : RoomMemberProfileViewEvents()
    data class ShowPowerLevelDemoteWarning(val currentValue: Int, val newValue: Int) : RoomMemberProfileViewEvents()

    data class StartVerification(
            val userId: String,
            val canCrossSign: Boolean
    ) : RoomMemberProfileViewEvents()

    data class ShareRoomMemberProfile(val permalink: String) : RoomMemberProfileViewEvents()
    data class OpenRoom(val roomId: String, val isNew: Boolean = false) : RoomMemberProfileViewEvents()
}
