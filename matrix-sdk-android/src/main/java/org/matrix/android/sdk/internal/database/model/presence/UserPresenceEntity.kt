

package org.matrix.android.sdk.internal.database.model.presence

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.matrix.android.sdk.api.session.presence.model.PresenceEnum
import org.matrix.android.sdk.api.session.presence.model.UserPresence

internal open class UserPresenceEntity(@PrimaryKey var userId: String = "",
                                       var lastActiveAgo: Long? = null,
                                       var statusMessage: String? = null,
                                       var isCurrentlyActive: Boolean? = null,
                                       var avatarUrl: String? = null,
                                       var displayName: String? = null
) : RealmObject() {

    var presence: PresenceEnum
        get() {
            return PresenceEnum.valueOf(presenceStr)
        }
        set(value) {
            presenceStr = value.name
        }

    private var presenceStr: String = PresenceEnum.UNAVAILABLE.name

    companion object
}

internal fun UserPresenceEntity.toUserPresence() =
        UserPresence(
                lastActiveAgo,
                statusMessage,
                isCurrentlyActive,
                presence
        )
