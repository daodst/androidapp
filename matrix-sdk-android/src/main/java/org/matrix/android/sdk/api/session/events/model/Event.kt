

package org.matrix.android.sdk.api.session.events.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.json.JSONObject
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import org.matrix.android.sdk.api.session.crypto.model.OlmDecryptionResult
import org.matrix.android.sdk.api.session.events.model.content.EncryptedEventContent
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessagePollContent
import org.matrix.android.sdk.api.session.room.model.message.MessageStickerContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent
import org.matrix.android.sdk.api.session.room.model.relation.shouldRenderInThread
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.threads.ThreadDetails
import org.matrix.android.sdk.api.util.ContentUtils
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.api.util.MatrixJsonParser
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.session.presence.model.PresenceContent
import timber.log.Timber

typealias Content = JsonDict


inline fun <reified T> Content?.toModel(catchError: Boolean = true): T? {
    val moshi = MatrixJsonParser.getMoshi()
    val moshiAdapter = moshi.adapter(T::class.java)
    return try {
        moshiAdapter.fromJsonValue(this)
    } catch (e: Exception) {
        if (catchError) {
            Timber.e(e, "To model failed : $e")
            null
        } else {
            throw e
        }
    }
}


@Suppress("UNCHECKED_CAST")
inline fun <reified T> T.toContent(): Content {
    val moshi = MatrixJsonParser.getMoshi()
    val moshiAdapter = moshi.adapter(T::class.java)
    return moshiAdapter.toJsonValue(this) as Content
}


@JsonClass(generateAdapter = true)
data class Event(
        @Json(name = "type") val type: String? = null,
        @Json(name = "event_id") val eventId: String? = null,
        @Json(name = "content") val content: Content? = null,
        @Json(name = "prev_content") val prevContent: Content? = null,
        @Json(name = "origin_server_ts") val originServerTs: Long? = null,
        @Json(name = "sender") val senderId: String? = null,
        @Json(name = "state_key") val stateKey: String? = null,
        @Json(name = "room_id") val roomId: String? = null,
        @Json(name = "unsigned") val unsignedData: UnsignedData? = null,
        @Json(name = "redacts") val redacts: String? = null
) {

    @Transient
    var mxDecryptionResult: OlmDecryptionResult? = null

    @Transient
    var mCryptoError: MXCryptoError.ErrorType? = null

    @Transient
    var mCryptoErrorReason: String? = null

    @Transient
    var sendState: SendState = SendState.UNKNOWN

    @Transient
    var sendStateDetails: String? = null

    @Transient
    var threadDetails: ThreadDetails? = null

    fun sendStateError(): MatrixError? {
        return sendStateDetails?.let {
            val matrixErrorAdapter = MoshiProvider.providesMoshi().adapter(MatrixError::class.java)
            tryOrNull { matrixErrorAdapter.fromJson(it) }
        }
    }

    
    @Transient
    var ageLocalTs: Long? = null

    
    fun copyAll(): Event {
        return copy().also {
            it.mxDecryptionResult = mxDecryptionResult
            it.mCryptoError = mCryptoError
            it.mCryptoErrorReason = mCryptoErrorReason
            it.sendState = sendState
            it.ageLocalTs = ageLocalTs
            it.threadDetails = threadDetails
        }
    }

    
    fun isStateEvent(): Boolean {
        return stateKey != null
    }

    
    
    

    
    fun isEncrypted(): Boolean {
        return type == EventType.ENCRYPTED
    }

    
    fun getSenderKey(): String? {
        return mxDecryptionResult?.senderKey
    }

    
    fun getKeysClaimed(): Map<String, String> {
        return mxDecryptionResult?.keysClaimed ?: HashMap()
    }

    
    fun getClearType(): String {
        return mxDecryptionResult?.payload?.get("type")?.toString() ?: type ?: EventType.MISSING_TYPE
    }

    
    fun getClearContent(): Content? {
        @Suppress("UNCHECKED_CAST")
        return mxDecryptionResult?.payload?.get("content") as? Content ?: content
    }

    fun toContentStringWithIndent(): String {
        val contentMap = toContent()
        return JSONObject(contentMap).toString(4)
    }

    fun toClearContentStringWithIndent(): String? {
        val contentMap = this.mxDecryptionResult?.payload
        val adapter = MoshiProvider.providesMoshi().adapter(Map::class.java)
        return contentMap?.let { JSONObject(adapter.toJson(it)).toString(4) }
    }

    
    fun getDecryptedTextSummary(): String? {
        if (isRedacted()) return "Message Deleted"
        val text = getDecryptedValue() ?: run {
            if (isPoll()) {
                return getPollQuestion() ?: "created a poll."
            }
            return null
        }

        return when {
            isReplyRenderedInThread() || isQuote() -> ContentUtils.extractUsefulTextFromReply(text)
            isFileMessage()                        -> "sent a file."
            isAudioMessage()                       -> "sent an audio file."
            isImageMessage()                       -> "sent an image."
            isVideoMessage()                       -> "sent a video."
            isSticker()                            -> "sent a sticker"
            isPoll()                               -> getPollQuestion() ?: "created a poll."
            else                                   -> text
        }
    }

    private fun Event.isQuote(): Boolean {
        if (isReplyRenderedInThread()) return false
        return getDecryptedValue("formatted_body")?.contains("<blockquote>") ?: false
    }

    
    fun isUserMentioned(userId: String): Boolean {
        return getDecryptedValue("formatted_body")?.contains(userId) ?: false
    }

    
    private fun getDecryptedValue(key: String = "body"): String? {
        return if (isEncrypted()) {
            @Suppress("UNCHECKED_CAST")
            val decryptedContent = mxDecryptionResult?.payload?.get("content") as? JsonDict
            decryptedContent?.get(key) as? String
        } else {
            content?.get(key) as? String
        }
    }

    
    fun isRedacted() = unsignedData?.redactedEvent != null

    
    fun isRedactedBySameUser() = senderId == unsignedData?.redactedEvent?.senderId

    fun resolvedPrevContent(): Content? = prevContent ?: unsignedData?.prevContent

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (type != other.type) return false
        if (eventId != other.eventId) return false
        if (content != other.content) return false
        if (prevContent != other.prevContent) return false
        if (originServerTs != other.originServerTs) return false
        if (senderId != other.senderId) return false
        if (stateKey != other.stateKey) return false
        if (roomId != other.roomId) return false
        if (unsignedData != other.unsignedData) return false
        if (redacts != other.redacts) return false
        if (mxDecryptionResult != other.mxDecryptionResult) return false
        if (mCryptoError != other.mCryptoError) return false
        if (mCryptoErrorReason != other.mCryptoErrorReason) return false
        if (sendState != other.sendState) return false
        if (threadDetails != other.threadDetails) return false
        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (eventId?.hashCode() ?: 0)
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (prevContent?.hashCode() ?: 0)
        result = 31 * result + (originServerTs?.hashCode() ?: 0)
        result = 31 * result + (senderId?.hashCode() ?: 0)
        result = 31 * result + (stateKey?.hashCode() ?: 0)
        result = 31 * result + (roomId?.hashCode() ?: 0)
        result = 31 * result + (unsignedData?.hashCode() ?: 0)
        result = 31 * result + (redacts?.hashCode() ?: 0)
        result = 31 * result + (mxDecryptionResult?.hashCode() ?: 0)
        result = 31 * result + (mCryptoError?.hashCode() ?: 0)
        result = 31 * result + (mCryptoErrorReason?.hashCode() ?: 0)
        result = 31 * result + sendState.hashCode()
        result = 31 * result + threadDetails.hashCode()

        return result
    }
}


fun Event.getMsgType(): String? {
    if (getClearType() != EventType.MESSAGE) return null
    return getClearContent()?.get(MessageContent.MSG_TYPE_JSON_KEY) as? String
}

fun Event.isTextMessage(): Boolean {
    return when (getMsgType()) {
        MessageType.MSGTYPE_RED_PACKET,
        MessageType.MSGTYPE_TEXT,
        MessageType.MSGTYPE_GIFTS,
        MessageType.MSGTYPE_EMOTE,
        MessageType.MSGTYPE_NOTICE -> true
        else                       -> false
    }
}

fun Event.isImageMessage(): Boolean {
    return when (getMsgType()) {
        MessageType.MSGTYPE_IMAGE -> true
        else                      -> false
    }
}

fun Event.isVideoMessage(): Boolean {
    return when (getMsgType()) {
        MessageType.MSGTYPE_VIDEO -> true
        else                      -> false
    }
}

fun Event.isAudioMessage(): Boolean {
    return when (getMsgType()) {
        MessageType.MSGTYPE_AUDIO -> true
        else                      -> false
    }
}

fun Event.isFileMessage(): Boolean {
    return when (getMsgType()) {
        MessageType.MSGTYPE_FILE -> true
        else                     -> false
    }
}

fun Event.isAttachmentMessage(): Boolean {
    return when (getMsgType()) {
        MessageType.MSGTYPE_IMAGE,
        MessageType.MSGTYPE_AUDIO,
        MessageType.MSGTYPE_VIDEO,
        MessageType.MSGTYPE_FILE -> true
        else                     -> false
    }
}

fun Event.getLocalMsgType(): String? {
    if (getClearType() != EventType.MESSAGE_LOCAL) return null
    return getClearContent()?.get(MessageContent.MSG_TYPE_JSON_KEY) as? String
}

fun Event.isLocalNotify(): Boolean {
    return when (getLocalMsgType()) {
        MessageType.MSGTYPE_JOIN_TEXT,
        MessageType.MSGTYPE_AWARD_TEXT,
        MessageType.MSGTYPE_PLEDGE_AWARD_TEXT,
        MessageType.MSGTYPE_CLUSTER_TEXT,
        MessageType.MSGTYPE_DPOS_TEXT,
        MessageType.MSGTYPE_DPOSOVER_TEXT,
        MessageType.MSGTYPE_LORD_TEXT -> true
        else                          -> false
    }
}

fun Event.isLocalMsg(): Boolean {
    return getClearType() == EventType.MESSAGE_LOCAL
}

fun Event.isLocationMessage(): Boolean {
    return when (getMsgType()) {
        MessageType.MSGTYPE_LOCATION -> true
        else                         -> false
    }
}

fun Event.isPoll(): Boolean = getClearType() in EventType.POLL_START || getClearType() in EventType.POLL_END

fun Event.isSticker(): Boolean = getClearType() == EventType.STICKER

fun Event.getRelationContent(): RelationDefaultContent? {
    return if (isEncrypted()) {
        content.toModel<EncryptedEventContent>()?.relatesTo
    } else {
        content.toModel<MessageContent>()?.relatesTo ?: run {
            
            if (getClearType() == EventType.STICKER) {
                getClearContent().toModel<MessageStickerContent>()?.relatesTo
            } else {
                null
            }
        }
    }
}


fun Event.getPollQuestion(): String? =
        getPollContent()?.getBestPollCreationInfo()?.question?.getBestQuestion()


fun Event.getRelationContentForType(type: String): RelationDefaultContent? =
        getRelationContent()?.takeIf { it.type == type }

fun Event.isReply(): Boolean {
    return getRelationContent()?.inReplyTo?.eventId != null
}

fun Event.isReplyRenderedInThread(): Boolean {
    return isReply() && getRelationContent()?.shouldRenderInThread() == true
}

fun Event.isThread(): Boolean = getRelationContentForType(RelationType.THREAD)?.eventId != null

fun Event.getRootThreadEventId(): String? = getRelationContentForType(RelationType.THREAD)?.eventId

fun Event.isEdition(): Boolean {
    return getRelationContentForType(RelationType.REPLACE)?.eventId != null
}

internal fun Event.getPresenceContent(): PresenceContent? {
    return content.toModel<PresenceContent>()
}

fun Event.isInvitation(): Boolean = type == EventType.STATE_ROOM_MEMBER &&
        content?.toModel<RoomMemberContent>()?.membership == Membership.INVITE

fun Event.getPollContent(): MessagePollContent? {
    return content.toModel<MessagePollContent>()
}

fun Event.supportsNotification() =
        this.getClearType() in EventType.MESSAGE + EventType.POLL_START + EventType.STATE_ROOM_BEACON_INFO
