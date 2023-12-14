

package org.matrix.android.sdk.api.session.room.model

data class ReadReceipt(
        val roomMember: RoomMemberSummary,
        val originServerTs: Long
)
