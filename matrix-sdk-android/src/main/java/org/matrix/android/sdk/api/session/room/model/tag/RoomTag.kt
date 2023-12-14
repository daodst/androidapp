

package org.matrix.android.sdk.api.session.room.model.tag

data class RoomTag(
        val name: String,
        val order: Double?
) {

    companion object {
        const val ROOM_TAG_FAVOURITE = "m.favourite"
        const val ROOM_TAG_LOW_PRIORITY = "m.lowpriority"
        const val ROOM_TAG_SERVER_NOTICE = "m.server_notice"
    }
}
