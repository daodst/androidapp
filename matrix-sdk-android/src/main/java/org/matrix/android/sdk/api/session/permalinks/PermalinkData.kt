

package org.matrix.android.sdk.api.session.permalinks

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


sealed class PermalinkData {

    data class RoomLink(
            val roomIdOrAlias: String,
            val isRoomAlias: Boolean,
            val eventId: String?,
            val viaParameters: List<String>
    ) : PermalinkData()

    
    @Parcelize
    data class RoomEmailInviteLink(
            val roomId: String,
            val email: String,
            val signUrl: String,
            val roomName: String?,
            val roomAvatarUrl: String?,
            val inviterName: String?,
            val identityServer: String,
            val token: String,
            val privateKey: String,
            val roomType: String?
    ) : PermalinkData(), Parcelable

    data class UserLink(val userId: String) : PermalinkData()

    data class GroupLink(val groupId: String) : PermalinkData()

    data class FallbackLink(val uri: Uri) : PermalinkData()
}
