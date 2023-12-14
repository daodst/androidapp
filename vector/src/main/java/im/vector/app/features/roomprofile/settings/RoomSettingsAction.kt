

package im.vector.app.features.roomprofile.settings

import android.app.Activity
import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.session.room.model.GuestAccess
import org.matrix.android.sdk.api.session.room.model.RoomHistoryVisibility
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules

sealed class RoomSettingsAction : VectorViewModelAction {
    data class SetAvatarAction(val avatarAction: RoomSettingsViewState.AvatarAction) : RoomSettingsAction()
    data class SetRoomName(val newName: String) : RoomSettingsAction()
    data class SetRoomTopic(val newTopic: String) : RoomSettingsAction()
    data class SetRoomHistoryVisibility(val visibility: RoomHistoryVisibility) : RoomSettingsAction()
    data class SetRoomJoinRule(val roomJoinRule: RoomJoinRules) : RoomSettingsAction()
    data class SetRoomGuestAccess(val guestAccess: GuestAccess) : RoomSettingsAction()
    data class Save(val activity: Activity) : RoomSettingsAction()

    
    object Cancel : RoomSettingsAction()
}
