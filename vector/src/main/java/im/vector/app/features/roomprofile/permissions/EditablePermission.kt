

package im.vector.app.features.roomprofile.permissions

import androidx.annotation.StringRes
import im.vector.app.R
import org.matrix.android.sdk.api.session.events.model.EventType


sealed class EditablePermission(@StringRes val labelResId: Int, @StringRes val spaceLabelResId: Int = labelResId) {
    
    open class EventTypeEditablePermission(val eventType: String,
                                           @StringRes labelResId: Int,
                                           @StringRes spaceLabelResId: Int = labelResId
    ) : EditablePermission(labelResId, spaceLabelResId)

    class ModifyWidgets : EventTypeEditablePermission(
            
            EventType.STATE_ROOM_WIDGET_LEGACY,
            R.string.room_permissions_modify_widgets
    )

    class ChangeRoomAvatar : EventTypeEditablePermission(
            EventType.STATE_ROOM_AVATAR,
            R.string.room_permissions_change_room_avatar,
            R.string.room_permissions_change_space_avatar
    )

    class ChangeMainAddressForTheRoom : EventTypeEditablePermission(
            EventType.STATE_ROOM_CANONICAL_ALIAS,
            R.string.room_permissions_change_main_address_for_the_room,
            R.string.room_permissions_change_main_address_for_the_space
    )

    class EnableRoomEncryption : EventTypeEditablePermission(
            EventType.STATE_ROOM_ENCRYPTION,
            R.string.room_permissions_enable_room_encryption,
            R.string.room_permissions_enable_space_encryption
    )

    class ChangeHistoryVisibility : EventTypeEditablePermission(
            EventType.STATE_ROOM_HISTORY_VISIBILITY,
            R.string.room_permissions_change_history_visibility
    )

    class ChangeRoomName : EventTypeEditablePermission(
            EventType.STATE_ROOM_NAME,
            R.string.room_permissions_change_room_name,
            R.string.room_permissions_change_space_name
    )

    class ChangePermissions : EventTypeEditablePermission(
            EventType.STATE_ROOM_POWER_LEVELS,
            R.string.room_permissions_change_permissions
    )

    class SendRoomServerAclEvents : EventTypeEditablePermission(
            EventType.STATE_ROOM_SERVER_ACL,
            R.string.room_permissions_send_m_room_server_acl_events
    )

    class UpgradeTheRoom : EventTypeEditablePermission(
            EventType.STATE_ROOM_TOMBSTONE,
            R.string.room_permissions_upgrade_the_room,
            R.string.room_permissions_upgrade_the_space
    )

    class ChangeTopic : EventTypeEditablePermission(
            EventType.STATE_ROOM_TOPIC,
            R.string.room_permissions_change_topic
    )

    
    class DefaultRole : EditablePermission(R.string.room_permissions_default_role)

    
    class SendMessages : EditablePermission(R.string.room_permissions_send_messages)

    
    class InviteUsers : EditablePermission(R.string.room_permissions_invite_users)

    
    class ChangeSettings : EditablePermission(R.string.room_permissions_change_settings)

    
    class KickUsers : EditablePermission(R.string.room_permissions_remove_users)

    
    class BanUsers : EditablePermission(R.string.room_permissions_ban_users)

    
    class RemoveMessagesSentByOthers : EditablePermission(R.string.room_permissions_remove_messages_sent_by_others)

    
    class NotifyEveryone : EditablePermission(R.string.room_permissions_notify_everyone)
}
