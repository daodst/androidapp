

package org.matrix.android.sdk.api.pushrules

enum class Kind(val value: String) {
    EventMatch("event_match"),
    ContainsDisplayName("contains_display_name"),
    RoomMemberCount("room_member_count"),
    SenderNotificationPermission("sender_notification_permission"),
    Unrecognised("");

    companion object {

        fun fromString(value: String): Kind {
            return when (value) {
                "event_match"                    -> EventMatch
                "contains_display_name"          -> ContainsDisplayName
                "room_member_count"              -> RoomMemberCount
                "sender_notification_permission" -> SenderNotificationPermission
                else                             -> Unrecognised
            }
        }
    }
}
