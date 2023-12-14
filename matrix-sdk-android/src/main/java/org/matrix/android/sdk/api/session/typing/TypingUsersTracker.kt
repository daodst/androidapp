

package org.matrix.android.sdk.api.session.typing

import org.matrix.android.sdk.api.session.room.sender.SenderInfo


interface TypingUsersTracker {

    
    fun getTypingUsers(roomId: String): List<SenderInfo>
}
