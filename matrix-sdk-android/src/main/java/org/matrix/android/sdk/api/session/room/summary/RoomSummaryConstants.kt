

package org.matrix.android.sdk.api.session.room.summary

import org.matrix.android.sdk.api.session.events.model.EventType

object RoomSummaryConstants {

    
    val PREVIEWABLE_TYPES = listOf(
            
            EventType.MESSAGE,
            EventType.MESSAGE_LOCAL,
            EventType.CALL_INVITE,
            EventType.CALL_HANGUP,
            EventType.CALL_REJECT,
            EventType.CALL_ANSWER,
            EventType.ENCRYPTED,
            EventType.STICKER,
            EventType.REACTION
    ) + EventType.POLL_START + EventType.STATE_ROOM_BEACON_INFO
}
