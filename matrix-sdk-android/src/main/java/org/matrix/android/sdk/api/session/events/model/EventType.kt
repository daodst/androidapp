

package org.matrix.android.sdk.api.session.events.model


object EventType {
    
    const val MISSING_TYPE = "org.matrix.android.sdk.missing_type"

    const val PRESENCE = "m.presence"
    const val MESSAGE = "m.room.message"
    const val MESSAGE_LOCAL = "m.lcoal.room.message"
    const val STICKER = "m.sticker"
    const val ENCRYPTED = "m.room.encrypted"
    const val FEEDBACK = "m.room.message.feedback"
    const val TYPING = "m.typing"
    const val REDACTION = "m.room.redaction"
    const val RECEIPT = "m.receipt"
    const val ROOM_KEY = "m.room_key"
    const val PLUMBING = "m.room.plumbing"
    const val BOT_OPTIONS = "m.room.bot.options"
    const val PREVIEW_URLS = "org.matrix.room.preview_urls"

    

    const val STATE_ROOM_WIDGET_LEGACY = "im.vector.modular.widgets"
    const val STATE_ROOM_WIDGET = "m.widget"
    const val STATE_ROOM_NAME = "m.room.name"
    const val STATE_ROOM_TOPIC = "m.room.topic"
    const val STATE_ROOM_AVATAR = "m.room.avatar"
    const val STATE_ROOM_MEMBER = "m.room.member"
    const val STATE_ROOM_THIRD_PARTY_INVITE = "m.room.third_party_invite"
    const val STATE_ROOM_CREATE = "m.room.create"
    const val STATE_ROOM_JOIN_RULES = "m.room.join_rules"
    const val STATE_ROOM_GUEST_ACCESS = "m.room.guest_access"
    const val STATE_ROOM_POWER_LEVELS = "m.room.power_levels"
    val STATE_ROOM_BEACON_INFO = listOf("org.matrix.msc3672.beacon_info", "m.beacon_info")
    val BEACON_LOCATION_DATA = listOf("org.matrix.msc3672.beacon", "m.beacon")

    const val STATE_SPACE_CHILD = "m.space.child"

    const val STATE_SPACE_PARENT = "m.space.parent"

    
    const val STATE_ROOM_ALIASES = "m.room.aliases"
    const val STATE_ROOM_TOMBSTONE = "m.room.tombstone"
    const val STATE_ROOM_CANONICAL_ALIAS = "m.room.canonical_alias"
    const val STATE_ROOM_HISTORY_VISIBILITY = "m.room.history_visibility"
    const val STATE_ROOM_RELATED_GROUPS = "m.room.related_groups"
    const val STATE_ROOM_PINNED_EVENT = "m.room.pinned_events"
    const val STATE_ROOM_ENCRYPTION = "m.room.encryption"
    const val STATE_ROOM_SERVER_ACL = "m.room.server_acl"

    
    const val CALL_INVITE = "m.call.invite"
    const val CALL_CANDIDATES = "m.call.candidates"
    const val CALL_ANSWER = "m.call.answer"
    const val CALL_SELECT_ANSWER = "m.call.select_answer"
    const val CALL_NEGOTIATE = "m.call.negotiate"
    const val CALL_REJECT = "m.call.reject"
    const val CALL_HANGUP = "m.call.hangup"
    const val CALL_ASSERTED_IDENTITY = "m.call.asserted_identity"
    const val CALL_ASSERTED_IDENTITY_PREFIX = "org.matrix.call.asserted_identity"

    
    const val CALL_REPLACES = "m.call.replaces"

    
    const val ROOM_KEY_REQUEST = "m.room_key_request"
    const val FORWARDED_ROOM_KEY = "m.forwarded_room_key"
    const val ROOM_KEY_WITHHELD = "org.matrix.room_key.withheld"

    const val REQUEST_SECRET = "m.secret.request"
    const val SEND_SECRET = "m.secret.send"

    
    const val KEY_VERIFICATION_START = "m.key.verification.start"
    const val KEY_VERIFICATION_ACCEPT = "m.key.verification.accept"
    const val KEY_VERIFICATION_KEY = "m.key.verification.key"
    const val KEY_VERIFICATION_MAC = "m.key.verification.mac"
    const val KEY_VERIFICATION_CANCEL = "m.key.verification.cancel"
    const val KEY_VERIFICATION_DONE = "m.key.verification.done"
    const val KEY_VERIFICATION_READY = "m.key.verification.ready"

    
    const val REACTION = "m.reaction"

    
    val POLL_START = listOf("org.matrix.msc3381.poll.start", "m.poll.start")
    val POLL_RESPONSE = listOf("org.matrix.msc3381.poll.response", "m.poll.response")
    val POLL_END = listOf("org.matrix.msc3381.poll.end", "m.poll.end")

    
    internal const val DUMMY = "m.dummy"

    fun isCallEvent(type: String): Boolean {
        return type == CALL_INVITE ||
                type == CALL_CANDIDATES ||
                type == CALL_ANSWER ||
                type == CALL_HANGUP ||
                type == CALL_SELECT_ANSWER ||
                type == CALL_NEGOTIATE ||
                type == CALL_REJECT ||
                type == CALL_REPLACES
    }
}
