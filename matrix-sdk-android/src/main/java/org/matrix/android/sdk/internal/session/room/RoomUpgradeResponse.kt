

package org.matrix.android.sdk.internal.session.room

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RoomUpgradeResponse(
        @Json(name = "replacement_room")
        val replacementRoomId: String
)
