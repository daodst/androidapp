

package org.matrix.android.sdk.api.session.room.timeline

import org.matrix.android.sdk.BuildConfig
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.api.session.events.model.getRelationContent
import org.matrix.android.sdk.api.session.events.model.isEdition
import org.matrix.android.sdk.api.session.events.model.isPoll
import org.matrix.android.sdk.api.session.events.model.isReply
import org.matrix.android.sdk.api.session.events.model.isSticker
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.EventAnnotationsSummary
import org.matrix.android.sdk.api.session.room.model.ReadReceipt
import org.matrix.android.sdk.api.session.room.model.livelocation.LiveLocationBeaconContent
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessagePollContent
import org.matrix.android.sdk.api.session.room.model.message.MessageStickerContent
import org.matrix.android.sdk.api.session.room.model.message.MessageTextContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent
import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import org.matrix.android.sdk.api.util.ContentUtils
import org.matrix.android.sdk.api.util.ContentUtils.extractUsefulTextFromReply


data class TimelineEvent(
        val root: Event,
        
        val localId: Long,
        val eventId: String,
        
        val displayIndex: Int,
        val ownedByThreadChunk: Boolean = false,
        val senderInfo: SenderInfo,
        val annotations: EventAnnotationsSummary? = null,
        val readReceipts: List<ReadReceipt> = emptyList()
) {

    init {
        if (BuildConfig.DEBUG) {
            assert(eventId == root.eventId)
        }
    }

    val roomId = root.roomId ?: ""

    val metadata = HashMap<String, Any>()

    
    fun enrichWith(key: String?, data: Any?) {
        if (key == null || data == null) {
            return
        }
        if (!metadata.containsKey(key)) {
            metadata[key] = data
        }
    }

    
    inline fun <reified T> getMetadata(key: String): T? {
        return metadata[key] as T?
    }

    fun isEncrypted(): Boolean {
        
        return EventType.ENCRYPTED == root.type
    }
}


fun TimelineEvent.hasBeenEdited() = annotations?.editSummary != null


fun TimelineEvent.getLatestEventId(): String {
    return annotations
            ?.editSummary
            ?.sourceEvents
            ?.lastOrNull()
            ?: eventId
}


fun TimelineEvent.getRelationContent(): RelationDefaultContent? {
    return root.getRelationContent()
}


fun TimelineEvent.getEditedEventId(): String? {
    return getRelationContent()?.takeIf { it.type == RelationType.REPLACE }?.eventId
}


fun TimelineEvent.getLastMessageContent(): MessageContent? {
    return when (root.getClearType()) {
        EventType.STICKER                   -> root.getClearContent().toModel<MessageStickerContent>()
        in EventType.POLL_START             -> (annotations?.editSummary?.latestContent ?: root.getClearContent()).toModel<MessagePollContent>()
        in EventType.STATE_ROOM_BEACON_INFO -> (annotations?.editSummary?.latestContent ?: root.getClearContent()).toModel<LiveLocationBeaconContent>()
        else                                -> (annotations?.editSummary?.latestContent ?: root.getClearContent()).toModel()
    }
}

fun MessageContent?.isCus(): Boolean {
    if (this == null) return false
    return this.isLocal()
            
            || msgType == MessageType.MSGTYPE_VOTE_TEXT
            
            || msgType == MessageType.MSGTYPE_TEXT_CREATE_CLUSTER
            
            || msgType == MessageType.MSGTYPE_TEXT_WELCOME
}

fun MessageContent?.isLocal(): Boolean {
    if (this == null) return false
    return msgType == MessageType.MSGTYPE_JOIN_TEXT
            || msgType == MessageType.MSGTYPE_AWARD_TEXT
            || msgType == MessageType.MSGTYPE_CLUSTER_TEXT
            || msgType == MessageType.MSGTYPE_DPOS_TEXT
            || msgType == MessageType.MSGTYPE_DPOSOVER_TEXT
            || msgType == MessageType.MSGTYPE_LORD_TEXT
            || msgType == MessageType.MSGTYPE_PLEDGE_AWARD_TEXT
            || msgType == MessageType.MSGTYPE_DAO_TEXT
            || msgType == MessageType.MSGTYPE_POS_TEXT
            || msgType == MessageType.MSGTYPE_CHART_TEXT
            || msgType == MessageType.MSGTYPE_CUS_TEXT
            
            || msgType == MessageType.MSGTYPE_TEXT_WELCOME
            || msgType == MessageType.MSGTYPE_TEXT_CREATE_CLUSTER
}





fun TimelineEvent.isReply(): Boolean {
    return root.isReply()
}

fun TimelineEvent.isEdition(): Boolean {
    return root.isEdition()
}

fun TimelineEvent.isPoll(): Boolean =
        root.isPoll()

fun TimelineEvent.isSticker(): Boolean {
    return root.isSticker()
}


fun TimelineEvent.isRootThread(): Boolean {
    return root.threadDetails?.isRootThread.orFalse()
}


fun TimelineEvent.getTextEditableContent(): String {
    val lastContentBody = getLastMessageContent()?.body ?: return ""
    return if (isReply()) {
        extractUsefulTextFromReply(lastContentBody)
    } else {
        lastContentBody
    }
}


fun MessageContent.getTextDisplayableContent(): String {
    return newContent?.toModel<MessageTextContent>()?.matrixFormattedBody?.let { ContentUtils.formatSpoilerTextFromHtml(it) }
            ?: newContent?.toModel<MessageContent>()?.body
            ?: (this as MessageTextContent?)?.matrixFormattedBody?.let { ContentUtils.formatSpoilerTextFromHtml(it) }
            ?: body
}
