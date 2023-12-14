

package org.matrix.android.sdk.api.session.presence

import org.matrix.android.sdk.api.session.presence.model.PresenceEnum
import org.matrix.android.sdk.api.session.presence.model.UserPresence


interface PresenceService {
    
    suspend fun setMyPresence(presence: PresenceEnum, statusMsg: String? = null)

    
    suspend fun fetchPresence(userId: String): UserPresence

    
}
