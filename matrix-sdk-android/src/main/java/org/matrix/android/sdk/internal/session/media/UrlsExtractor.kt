

package org.matrix.android.sdk.internal.session.media

import android.util.Patterns
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.room.timeline.isReply
import org.matrix.android.sdk.api.util.ContentUtils
import javax.inject.Inject

internal class UrlsExtractor @Inject constructor() {
    
    private val urlRegex = Patterns.WEB_URL.toRegex()

    fun extract(event: TimelineEvent): List<String> {
        return event.takeIf { it.root.getClearType() == EventType.MESSAGE }
                ?.getLastMessageContent()
                ?.takeIf {
                    it.msgType == MessageType.MSGTYPE_TEXT ||
                            it.msgType == MessageType.MSGTYPE_NOTICE ||
                            it.msgType == MessageType.MSGTYPE_EMOTE
                }
                ?.let { messageContent ->
                    if (event.isReply()) {
                        
                        ContentUtils.extractUsefulTextFromReply(messageContent.body)
                    } else {
                        messageContent.body
                    }
                }
                ?.let { urlRegex.findAll(it) }
                ?.map { it.value }
                ?.filter { it.startsWith("https://") || it.startsWith("http://") }
                ?.distinct()
                ?.toList()
                .orEmpty()
    }
}
