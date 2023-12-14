

package im.vector.app.features.home.room.detail.timeline.format

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.text.Spannable
import android.text.style.DynamicDrawableSpan
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import dagger.Lazy
import im.vector.app.EmojiSpanify
import im.vector.app.R
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.features.home.room.detail.TIMELINE_AWARD_TYPE_AIRDROP
import im.vector.app.features.home.room.detail.TIMELINE_AWARD_TYPE_DVM
import im.vector.app.features.home.room.detail.TIMELINE_AWARD_TYPE_POS
import im.vector.app.features.home.room.list.widget.ExImageSpan
import im.vector.app.features.html.EventHtmlRenderer
import im.vector.app.features.themes.ThemeUtils
import me.gujun.android.span.span
import org.commonmark.node.Document
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageAudioContent
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusAwardContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusPosContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusTxtContent
import org.matrix.android.sdk.api.session.room.model.message.MessagePollContent
import org.matrix.android.sdk.api.session.room.model.message.MessageTextContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.relation.ReactionContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.room.timeline.getTextDisplayableContent
import timber.log.Timber
import javax.inject.Inject

class DisplayableEventFormatter @Inject constructor(
        private val stringProvider: StringProvider,
        private val colorProvider: ColorProvider,
        private val emojiSpanify: EmojiSpanify,
        private val context: Context,
        private val dimensionConverter: DimensionConverter,
        private val noticeEventFormatter: NoticeEventFormatter,
        private val htmlRenderer: Lazy<EventHtmlRenderer>
) {

    
    fun format(timelineEvent: TimelineEvent, isDm: Boolean, appendAuthor: Boolean, atMsgEmpty: Boolean = true): CharSequence {
        if (timelineEvent.root.isRedacted()) {
            return noticeEventFormatter.formatRedactedEvent(timelineEvent.root)
        }

        if (timelineEvent.root.isEncrypted() &&
                timelineEvent.root.mxDecryptionResult == null) {
            return stringProvider.getString(R.string.encrypted_message)
        }

        val senderName = timelineEvent.senderInfo.disambiguatedDisplayName

        return when (timelineEvent.root.getClearType()) {
            EventType.MESSAGE_LOCAL             -> {
                timelineEvent.getLastMessageContent()?.let { messageContent ->

                    when (messageContent.msgType) {

                        MessageType.MSGTYPE_CUS_TEXT   -> {
                            (messageContent as MessageCusTxtContent).listTitle
                        }
                        
                        MessageType.MSGTYPE_LORD_TEXT,
                        MessageType.MSGTYPE_CLUSTER_TEXT,
                        MessageType.MSGTYPE_PLEDGE_AWARD_TEXT,
                        MessageType.MSGTYPE_DPOS_TEXT,
                        MessageType.MSGTYPE_DPOSOVER_TEXT,
                        MessageType.MSGTYPE_JOIN_TEXT  -> {
                            ""
                        }
                        MessageType.MSGTYPE_AWARD_TEXT -> {
                            val content = messageContent as MessageCusAwardContent
                            val type = content.type
                            if (type == TIMELINE_AWARD_TYPE_AIRDROP) {
                                getStr(stringProvider.getString(R.string.timeline_award_type_air_drop), atMsgEmpty = atMsgEmpty)
                            } else if (type == TIMELINE_AWARD_TYPE_POS) {
                                getStr(stringProvider.getString(R.string.timeline_award_type_pos), atMsgEmpty = atMsgEmpty)
                            } else if (type == TIMELINE_AWARD_TYPE_DVM) {
                                getStr(stringProvider.getString(R.string.timeline_award_type_dvm), atMsgEmpty = atMsgEmpty)
                            } else {
                                getStr(stringProvider.getString(R.string.timeline_award_type_dao), atMsgEmpty = atMsgEmpty)
                            }
                        }
                        MessageType.MSGTYPE_CHART_TEXT -> {
                            getStr(stringProvider.getString(R.string.msgtype_chart_text), atMsgEmpty = atMsgEmpty)
                        }

                        MessageType.MSGTYPE_VOTE_TEXT  -> {
                            getStr(stringProvider.getString(R.string.msgtype_vote_text), atMsgEmpty = atMsgEmpty)
                        }
                        MessageType.MSGTYPE_DAO_TEXT   -> {
                            
                            getStr(stringProvider.getString(R.string.msgtype_dao_text), atMsgEmpty = atMsgEmpty)
                        }
                        MessageType.MSGTYPE_POS_TEXT   -> {
                            (messageContent as MessageCusPosContent).listTitle
                        }
                        else                           -> {
                            simpleFormat(senderName, messageContent.body, appendAuthor)
                        }
                    }
                } ?: span { }
            }
            EventType.MESSAGE                   -> {
                timelineEvent.getLastMessageContent()?.let { messageContent ->
                    Timber.i("----RoomSummaryItemFactory---------------------${messageContent.msgType}-------")
                    when (messageContent.msgType) {
                        MessageType.MSGTYPE_TEXT_CREATE_CLUSTER  -> {
                            stringProvider.getString(R.string.cuscreate_cluster_title)
                        }
                        MessageType.MSGTYPE_TEXT_WELCOME         -> {
                            stringProvider.getString(R.string.cus_welcom_title)
                        }
                        MessageType.MSGTYPE_TEXT                 -> {
                            val body = messageContent.getTextDisplayableContent()
                            if (messageContent is MessageTextContent && messageContent.matrixFormattedBody.isNullOrBlank().not()) {
                                val localFormattedBody = htmlRenderer.get().parse(body) as Document
                                val renderedBody = htmlRenderer.get().render(localFormattedBody) ?: body
                                simpleFormat(senderName, renderedBody, appendAuthor)
                            } else {
                                simpleFormat(senderName, body, appendAuthor)
                            }
                        }
                        MessageType.MSGTYPE_VERIFICATION_REQUEST -> {
                            simpleFormat(senderName, stringProvider.getString(R.string.verification_request), appendAuthor)
                        }
                        MessageType.MSGTYPE_IMAGE                -> {
                            simpleFormat(senderName, stringProvider.getString(R.string.sent_an_image), appendAuthor)
                        }
                        MessageType.MSGTYPE_AUDIO                -> {
                            if ((messageContent as? MessageAudioContent)?.voiceMessageIndicator != null) {
                                simpleFormat(senderName, stringProvider.getString(R.string.sent_a_voice_message), appendAuthor)
                            } else {
                                simpleFormat(senderName, stringProvider.getString(R.string.sent_an_audio_file), appendAuthor)
                            }
                        }
                        MessageType.MSGTYPE_VIDEO                -> {
                            simpleFormat(senderName, stringProvider.getString(R.string.sent_a_video), appendAuthor)
                        }
                        MessageType.MSGTYPE_FILE                 -> {
                            simpleFormat(senderName, stringProvider.getString(R.string.sent_a_file), appendAuthor)
                        }
                        MessageType.MSGTYPE_LOCATION             -> {
                            simpleFormat(senderName, stringProvider.getString(R.string.sent_location), appendAuthor)
                        }
                        else                                     -> {
                            simpleFormat(senderName, messageContent.body, appendAuthor)
                        }
                    }
                } ?: span { }
            }
            EventType.STICKER                   -> {
                simpleFormat(senderName, stringProvider.getString(R.string.send_a_sticker), appendAuthor)
            }
            EventType.REACTION                  -> {
                timelineEvent.root.getClearContent().toModel<ReactionContent>()?.relatesTo?.let {
                    val emojiSpanned = emojiSpanify.spanify(stringProvider.getString(R.string.sent_a_reaction, it.key))
                    simpleFormat(senderName, emojiSpanned, appendAuthor)
                } ?: span { }
            }
            EventType.KEY_VERIFICATION_CANCEL,
            EventType.KEY_VERIFICATION_DONE     -> {
                
                simpleFormat(senderName, stringProvider.getString(R.string.sent_verification_conclusion), appendAuthor)
            }
            EventType.KEY_VERIFICATION_START,
            EventType.KEY_VERIFICATION_ACCEPT,
            EventType.KEY_VERIFICATION_MAC,
            EventType.KEY_VERIFICATION_KEY,
            EventType.KEY_VERIFICATION_READY,
            EventType.CALL_CANDIDATES           -> {
                span { }
            }
            in EventType.POLL_START             -> {
                timelineEvent.root.getClearContent().toModel<MessagePollContent>(catchError = true)?.getBestPollCreationInfo()?.question?.getBestQuestion()
                        ?: stringProvider.getString(R.string.sent_a_poll)
            }
            in EventType.POLL_RESPONSE          -> {
                stringProvider.getString(R.string.poll_response_room_list_preview)
            }
            in EventType.POLL_END               -> {
                stringProvider.getString(R.string.poll_end_room_list_preview)
            }
            in EventType.STATE_ROOM_BEACON_INFO -> {
                simpleFormat(senderName, stringProvider.getString(R.string.sent_live_location), appendAuthor)
            }
            else                                -> {
                span {
                    text = noticeEventFormatter.format(timelineEvent, isDm) ?: ""
                    textStyle = "italic"
                }
            }
        }
    }

    private fun getStr(value: CharSequence, showGifs: Boolean = false, atMsgEmpty: Boolean): CharSequence {

        if (showGifs) {
            val resources: Resources = context.getResources()
            val imgRes: Int = R.drawable.room_list_balance
            val bitmap = BitmapFactory.decodeResource(resources, imgRes)
            val dstWidth = dimensionConverter.dpToPx(22)
            val scaleBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstWidth, true)

            val drawable = BitmapDrawable(resources, scaleBitmap).apply {
                setBounds(0, 0, bitmap.width, bitmap.height)
            }
            val span = ExImageSpan(drawable, DynamicDrawableSpan.ALIGN_CENTER)

            return buildSpannedString {
                append("[")
                append(" ")
                setSpan(span, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(value)
                append("]")
            }
        } else {
            val color = if (atMsgEmpty) ThemeUtils.getColor(context, R.attr.vctr_content_secondary) else ContextCompat.getColor(context, R.color.color_FF7505)
            return span {
                textColor = color
                +"[$value]"
            }
        }
    }

    fun formatThreadSummary(
            event: Event?,
            latestEdition: String? = null): CharSequence {
        event ?: return ""

        
        if (latestEdition != null) {
            return run {
                val localFormattedBody = htmlRenderer.get().parse(latestEdition) as Document
                val renderedBody = htmlRenderer.get().render(localFormattedBody) ?: latestEdition
                renderedBody
            }
        }

        
        if (event.isRedacted()) {
            return noticeEventFormatter.formatRedactedEvent(event)
        }

        
        if (event.isEncrypted() &&
                event.mxDecryptionResult == null) {
            return stringProvider.getString(R.string.encrypted_message)
        }

        return when (event.getClearType()) {
            EventType.MESSAGE                   -> {
                (event.getClearContent().toModel() as? MessageContent)?.let { messageContent ->
                    when (messageContent.msgType) {
                        MessageType.MSGTYPE_TEXT                 -> {
                            val body = messageContent.getTextDisplayableContent()
                            if (messageContent is MessageTextContent && messageContent.matrixFormattedBody.isNullOrBlank().not()) {
                                val localFormattedBody = htmlRenderer.get().parse(body) as Document
                                val renderedBody = htmlRenderer.get().render(localFormattedBody) ?: body
                                renderedBody
                            } else {
                                body
                            }
                        }
                        MessageType.MSGTYPE_VERIFICATION_REQUEST -> {
                            stringProvider.getString(R.string.verification_request)
                        }
                        MessageType.MSGTYPE_IMAGE                -> {
                            stringProvider.getString(R.string.sent_an_image)
                        }
                        MessageType.MSGTYPE_AUDIO                -> {
                            if ((messageContent as? MessageAudioContent)?.voiceMessageIndicator != null) {
                                stringProvider.getString(R.string.sent_a_voice_message)
                            } else {
                                stringProvider.getString(R.string.sent_an_audio_file)
                            }
                        }
                        MessageType.MSGTYPE_VIDEO                -> {
                            stringProvider.getString(R.string.sent_a_video)
                        }
                        MessageType.MSGTYPE_FILE                 -> {
                            stringProvider.getString(R.string.sent_a_file)
                        }
                        MessageType.MSGTYPE_LOCATION             -> {
                            stringProvider.getString(R.string.sent_location)
                        }
                        else                                     -> {
                            messageContent.body
                        }
                    }
                } ?: span { }
            }
            EventType.STICKER                   -> {
                stringProvider.getString(R.string.send_a_sticker)
            }
            EventType.REACTION                  -> {
                event.getClearContent().toModel<ReactionContent>()?.relatesTo?.let {
                    emojiSpanify.spanify(stringProvider.getString(R.string.sent_a_reaction, it.key))
                } ?: span { }
            }
            in EventType.POLL_START             -> {
                event.getClearContent().toModel<MessagePollContent>(catchError = true)?.pollCreationInfo?.question?.question
                        ?: stringProvider.getString(R.string.sent_a_poll)
            }
            in EventType.POLL_RESPONSE          -> {
                stringProvider.getString(R.string.poll_response_room_list_preview)
            }
            in EventType.POLL_END               -> {
                stringProvider.getString(R.string.poll_end_room_list_preview)
            }
            in EventType.STATE_ROOM_BEACON_INFO -> {
                stringProvider.getString(R.string.sent_live_location)
            }
            else                                -> {
                span {
                }
            }
        }
    }

    private fun simpleFormat(senderName: String, body: CharSequence, appendAuthor: Boolean): CharSequence {
        return if (appendAuthor) {
            span {
                text = senderName
                textColor = colorProvider.getColorFromAttribute(R.attr.vctr_content_primary)
            }
                    .append(": ")
                    .append(body)
        } else {
            body
        }
    }
}
