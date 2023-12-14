

package org.matrix.android.sdk.internal.session.typing

import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import org.matrix.android.sdk.api.session.typing.TypingUsersTracker
import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject

@SessionScope
internal class DefaultTypingUsersTracker @Inject constructor() : TypingUsersTracker {

    private val typingUsers = mutableMapOf<String, List<SenderInfo>>()

    
    fun setTypingUsersFromRoom(roomId: String, senderInfoList: List<SenderInfo>) {
        val hasNewValue = typingUsers[roomId] != senderInfoList
        if (hasNewValue) {
            typingUsers[roomId] = senderInfoList
        }
    }

    override fun getTypingUsers(roomId: String): List<SenderInfo> {
        return typingUsers[roomId].orEmpty()
    }
}
