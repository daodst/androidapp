

package org.matrix.android.sdk.api.session.room.uploads

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.message.MessageWithAttachmentContent
import org.matrix.android.sdk.api.session.room.sender.SenderInfo


data class UploadEvent(
        val root: Event,
        val eventId: String,
        val contentWithAttachmentContent: MessageWithAttachmentContent,
        val senderInfo: SenderInfo
)
