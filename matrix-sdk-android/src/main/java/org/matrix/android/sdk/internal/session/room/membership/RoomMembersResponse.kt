

package org.matrix.android.sdk.internal.session.room.membership

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Event

@JsonClass(generateAdapter = true)
internal data class RoomMembersResponse(
        @Json(name = "chunk") val roomMemberEvents: List<Event>
)
