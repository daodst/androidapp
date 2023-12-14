

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
sealed class LazyRoomSyncEphemeral {
    data class Parsed(val _roomSyncEphemeral: RoomSyncEphemeral) : LazyRoomSyncEphemeral()
    object Stored : LazyRoomSyncEphemeral()
}
