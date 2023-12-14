

package org.matrix.android.sdk.internal.session.sync

import org.matrix.android.sdk.api.session.presence.model.PresenceEnum


internal enum class SyncPresence(val value: String) {
    Offline("offline"),
    Online("online"),
    Unavailable("unavailable");

    companion object {
        fun from(presenceEnum: PresenceEnum): SyncPresence {
            return when (presenceEnum) {
                PresenceEnum.ONLINE      -> Online
                PresenceEnum.OFFLINE     -> Offline
                PresenceEnum.UNAVAILABLE -> Unavailable
            }
        }

        fun from(s: String?): SyncPresence? = values().find { it.value == s }
    }
}
