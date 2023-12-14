

package im.vector.app.features.home.room.detail.timeline.format

import android.content.Context
import im.vector.app.core.utils.TextUtils
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.isAudioMessage
import org.matrix.android.sdk.api.session.events.model.isFileMessage
import org.matrix.android.sdk.api.session.events.model.isImageMessage
import org.matrix.android.sdk.api.session.events.model.isVideoMessage
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageAudioContent
import org.matrix.android.sdk.api.session.room.model.message.MessageFileContent
import org.matrix.android.sdk.api.session.room.model.message.MessageImageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageVideoContent
import org.threeten.bp.Duration
import javax.inject.Inject

class EventDetailsFormatter @Inject constructor(
        private val context: Context
) {

    fun format(event: Event?): CharSequence? {
        event ?: return null

        if (event.isRedacted()) {
            return null
        }

        if (event.isEncrypted() && event.mxDecryptionResult == null) {
            return null
        }

        return when {
            event.isImageMessage() -> formatForImageMessage(event)
            event.isVideoMessage() -> formatForVideoMessage(event)
            event.isAudioMessage() -> formatForAudioMessage(event)
            event.isFileMessage()  -> formatForFileMessage(event)
            else                   -> null
        }
    }

    
    private fun formatForImageMessage(event: Event): CharSequence? {
        return event.getClearContent().toModel<MessageImageContent>()?.info
                ?.let { "${it.width} x ${it.height} - ${it.size.asFileSize()}" }
    }

    
    private fun formatForVideoMessage(event: Event): CharSequence? {
        return event.getClearContent().toModel<MessageVideoContent>()?.videoInfo
                ?.let { "${it.duration.asDuration()} - ${it.width} x ${it.height} - ${it.size.asFileSize()}" }
    }

    
    private fun formatForAudioMessage(event: Event): CharSequence? {
        return event.getClearContent().toModel<MessageAudioContent>()?.audioInfo
                ?.let { audioInfo ->
                    listOfNotNull(audioInfo.duration?.asDuration(), audioInfo.size?.asFileSize())
                            .joinToString(" - ")
                            .takeIf { it.isNotEmpty() }
                }
    }

    
    private fun formatForFileMessage(event: Event): CharSequence? {
        return event.getClearContent().toModel<MessageFileContent>()?.info
                ?.let { "${it.size.asFileSize()} - ${it.mimeType}" }
    }

    private fun Long.asFileSize() = TextUtils.formatFileSize(context, this)
    private fun Int.asDuration() = TextUtils.formatDuration(Duration.ofMillis(toLong()))
}
