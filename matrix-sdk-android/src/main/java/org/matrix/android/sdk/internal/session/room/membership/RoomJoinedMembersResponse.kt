

package org.matrix.android.sdk.internal.session.room.membership

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RoomJoinedMembersResponse(
        @Json(name = "joined") val roomMemberEvents: Map<String, Map<String, String>>
)

data class RoomMember(val uId: String, val chat_addr: String)
