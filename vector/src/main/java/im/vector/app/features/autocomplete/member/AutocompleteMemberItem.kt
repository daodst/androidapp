

package im.vector.app.features.autocomplete.member

import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary

sealed class AutocompleteMemberItem {
    data class Header(val id: String, val title: String) : AutocompleteMemberItem()
    data class RoomMember(val roomMemberSummary: RoomMemberSummary) : AutocompleteMemberItem()
    data class Everyone(val roomSummary: RoomSummary) : AutocompleteMemberItem()
}
