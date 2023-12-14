

package im.vector.app.features.home.room.detail

import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

enum class ChatEffect {
    CONFETTI,
    SNOWFALL
}

fun ChatEffect.toMessageType(): String {
    return when (this) {
        ChatEffect.CONFETTI -> MessageType.MSGTYPE_CONFETTI
        ChatEffect.SNOWFALL -> MessageType.MSGTYPE_SNOWFALL
    }
}


class ChatEffectManager @Inject constructor() {

    interface Delegate {
        fun stopEffects()
        fun shouldStartEffect(effect: ChatEffect)
    }

    var delegate: Delegate? = null

    private var stopTimer: Timer? = null

    
    private val alreadyPlayed = mutableListOf<String>()

    fun checkForEffect(event: TimelineEvent) {
        val age = event.root.ageLocalTs ?: 0
        val now = System.currentTimeMillis()
        
        if ((now - age) >= 10_000) return
        val content = event.root.getClearContent()?.toModel<MessageContent>() ?: return
        val effect = findEffect(content, event)
        if (effect != null) {
            synchronized(this) {
                if (hasAlreadyPlayed(event)) return
                markAsAlreadyPlayed(event)
                
                if (stopTimer != null) return
                delegate?.shouldStartEffect(effect)
                stopTimer = Timer().apply {
                    schedule(object : TimerTask() {
                        override fun run() {
                            stopEffect()
                        }
                    }, 6_000)
                }
            }
        }
    }

    fun dispose() {
        stopTimer?.cancel()
        stopTimer = null
        alreadyPlayed.clear()
    }

    @Synchronized
    private fun stopEffect() {
        stopTimer = null
        delegate?.stopEffects()
    }

    private fun markAsAlreadyPlayed(event: TimelineEvent) {
        alreadyPlayed.add(event.eventId)
        
        event.root.unsignedData?.transactionId?.let {
            alreadyPlayed.add(it)
        }
    }

    private fun hasAlreadyPlayed(event: TimelineEvent): Boolean {
        return alreadyPlayed.contains(event.eventId) ||
                (event.root.unsignedData?.transactionId?.let { alreadyPlayed.contains(it) } ?: false)
    }

    private fun findEffect(content: MessageContent, event: TimelineEvent): ChatEffect? {
        return when (content.msgType) {
            MessageType.MSGTYPE_CONFETTI -> ChatEffect.CONFETTI
            MessageType.MSGTYPE_SNOWFALL -> ChatEffect.SNOWFALL
            MessageType.MSGTYPE_EMOTE,
            MessageType.MSGTYPE_TEXT     -> {
                event.root.getClearContent().toModel<MessageContent>()?.body
                        ?.let { text ->
                            when {
                                EMOJIS_FOR_CONFETTI.any { text.contains(it) } -> ChatEffect.CONFETTI
                                EMOJIS_FOR_SNOWFALL.any { text.contains(it) } -> ChatEffect.SNOWFALL
                                else                                          -> null
                            }
                        }
            }
            else                         -> null
        }
    }

    companion object {
        private val EMOJIS_FOR_CONFETTI = listOf(
                "🎉",
                "🎊"
        )
        private val EMOJIS_FOR_SNOWFALL = listOf(
                "⛄️",
                "☃️",
                "❄️"
        )
    }
}
