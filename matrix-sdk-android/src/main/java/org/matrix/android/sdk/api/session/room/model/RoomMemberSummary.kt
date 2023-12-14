

package org.matrix.android.sdk.api.session.room.model

import org.matrix.android.sdk.api.session.presence.model.UserPresence


data class RoomMemberSummary constructor(
        val membership: Membership,
        val userId: String,
        val userPresence: UserPresence? = null,
        val displayName: String? = null,
        val avatarUrl: String? = null
)
