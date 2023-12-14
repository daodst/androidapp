

package org.matrix.android.sdk.internal.session.events

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.RoomMemberContent

internal fun Event.getFixedRoomMemberContent(): RoomMemberContent? {
    val content = content.toModel<RoomMemberContent>()
    
    return if (content?.membership?.isLeft() == true) {
        val prevContent = resolvedPrevContent().toModel<RoomMemberContent>()
        content.copy(
                displayName = prevContent?.displayName,
                avatarUrl = prevContent?.avatarUrl
        )
    } else {
        content
    }
}
