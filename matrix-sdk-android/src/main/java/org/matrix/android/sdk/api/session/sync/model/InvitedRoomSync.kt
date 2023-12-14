

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InvitedRoomSync(

        
        @Json(name = "invite_state") val inviteState: RoomInviteState? = null
)
