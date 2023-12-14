

package org.matrix.android.sdk.api.session.room.send

enum class SendState {
    UNKNOWN,

    
    UNSENT,

    
    ENCRYPTING,

    
    SENDING,

    
    SENT,

    
    SYNCED,

    
    UNDELIVERED,

    
    FAILED_UNKNOWN_DEVICES;

    internal companion object {
        val HAS_FAILED_STATES = listOf(UNDELIVERED, FAILED_UNKNOWN_DEVICES)
        val IS_SENT_STATES = listOf(SENT, SYNCED)
        val IS_PROGRESSING_STATES = listOf(ENCRYPTING, SENDING)
        val IS_SENDING_STATES = IS_PROGRESSING_STATES + UNSENT
        val PENDING_STATES = IS_SENDING_STATES + HAS_FAILED_STATES
    }

    fun isSent() = IS_SENT_STATES.contains(this)

    fun hasFailed() = HAS_FAILED_STATES.contains(this)

    fun isInProgress() = IS_PROGRESSING_STATES.contains(this)

    fun isSending() = IS_SENDING_STATES.contains(this)
}
