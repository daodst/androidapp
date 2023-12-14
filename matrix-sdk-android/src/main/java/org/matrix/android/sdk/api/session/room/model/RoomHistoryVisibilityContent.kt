

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import timber.log.Timber

@JsonClass(generateAdapter = true)
data class RoomHistoryVisibilityContent(
        @Json(name = "history_visibility") val _historyVisibility: String? = null
) {
    val historyVisibility: RoomHistoryVisibility? = when (_historyVisibility) {
        "world_readable" -> RoomHistoryVisibility.WORLD_READABLE
        "shared"         -> RoomHistoryVisibility.SHARED
        "invited"        -> RoomHistoryVisibility.INVITED
        "joined"         -> RoomHistoryVisibility.JOINED
        else             -> {
            Timber.w("Invalid value for RoomHistoryVisibility: `$_historyVisibility`")
            null
        }
    }
}
