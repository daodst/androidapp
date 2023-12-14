

package org.matrix.android.sdk.api.session.room.accountdata

import org.matrix.android.sdk.api.session.events.model.Content


data class RoomAccountDataEvent(
        val roomId: String,
        val type: String,
        val content: Content
)
