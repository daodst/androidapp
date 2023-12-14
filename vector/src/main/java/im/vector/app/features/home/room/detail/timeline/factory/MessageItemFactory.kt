

package im.vector.app.features.home.room.detail.timeline.factory

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.View
import dagger.Lazy
import im.vector.app.R
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.files.LocalFilesHelper
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.core.utils.containsOnlyEmojis
import im.vector.app.features.home.room.detail.timeline.TimelineEventController
import im.vector.app.features.home.room.detail.timeline.helper.AudioMessagePlaybackTracker
import im.vector.app.features.home.room.detail.timeline.helper.AvatarSizeProvider
import im.vector.app.features.home.room.detail.timeline.helper.ContentDownloadStateTrackerBinder
import im.vector.app.features.home.room.detail.timeline.helper.ContentUploadStateTrackerBinder
import im.vector.app.features.home.room.detail.timeline.helper.LocationPinProvider
import im.vector.app.features.home.room.detail.timeline.helper.MessageInformationDataFactory
import im.vector.app.features.home.room.detail.timeline.helper.MessageItemAttributesFactory
import im.vector.app.features.home.room.detail.timeline.helper.TimelineMediaSizeProvider
import im.vector.app.features.home.room.detail.timeline.item.AbsMessageItem
import im.vector.app.features.home.room.detail.timeline.item.MessageAudioItem
import im.vector.app.features.home.room.detail.timeline.item.MessageAudioItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageCusChartItem
import im.vector.app.features.home.room.detail.timeline.item.MessageCusChartItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageCusClusterItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageCusDaoItem
import im.vector.app.features.home.room.detail.timeline.item.MessageCusDaoItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageCusDposItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageCusDposOverItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageCusJoinItem
import im.vector.app.features.home.room.detail.timeline.item.MessageCusJoinItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageCusLordItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageCusPledgeAwardItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageCusPosItem
import im.vector.app.features.home.room.detail.timeline.item.MessageCusPosItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageCusVoiceItem
import im.vector.app.features.home.room.detail.timeline.item.MessageCusVoiceItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageCusVoteItem
import im.vector.app.features.home.room.detail.timeline.item.MessageCusVoteItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageCusWelcomeItem
import im.vector.app.features.home.room.detail.timeline.item.MessageCusWelcomeItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageFileItem
import im.vector.app.features.home.room.detail.timeline.item.MessageFileItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageGiftsItem
import im.vector.app.features.home.room.detail.timeline.item.MessageGiftsItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageImageVideoItem
import im.vector.app.features.home.room.detail.timeline.item.MessageImageVideoItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageInformationData
import im.vector.app.features.home.room.detail.timeline.item.MessageLocationItem
import im.vector.app.features.home.room.detail.timeline.item.MessageLocationItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageRedPacketItem
import im.vector.app.features.home.room.detail.timeline.item.MessageRedPacketItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageTextItem
import im.vector.app.features.home.room.detail.timeline.item.MessageTextItem_
import im.vector.app.features.home.room.detail.timeline.item.MessageVoiceItem
import im.vector.app.features.home.room.detail.timeline.item.MessageVoiceItem_
import im.vector.app.features.home.room.detail.timeline.item.PollItem
import im.vector.app.features.home.room.detail.timeline.item.PollItem_
import im.vector.app.features.home.room.detail.timeline.item.PollOptionViewState.PollEnded
import im.vector.app.features.home.room.detail.timeline.item.PollOptionViewState.PollReady
import im.vector.app.features.home.room.detail.timeline.item.PollOptionViewState.PollSending
import im.vector.app.features.home.room.detail.timeline.item.PollOptionViewState.PollUndisclosed
import im.vector.app.features.home.room.detail.timeline.item.PollOptionViewState.PollVoted
import im.vector.app.features.home.room.detail.timeline.item.PollResponseData
import im.vector.app.features.home.room.detail.timeline.item.RedactedMessageItem
import im.vector.app.features.home.room.detail.timeline.item.RedactedMessageItem_
import im.vector.app.features.home.room.detail.timeline.item.SendStateDecoration
import im.vector.app.features.home.room.detail.timeline.item.VerificationRequestItem
import im.vector.app.features.home.room.detail.timeline.item.VerificationRequestItem_
import im.vector.app.features.home.room.detail.timeline.render.EventTextRenderer
import im.vector.app.features.home.room.detail.timeline.style.TimelineMessageLayout
import im.vector.app.features.home.room.detail.timeline.tools.createLinkMovementMethod
import im.vector.app.features.home.room.detail.timeline.tools.linkify
import im.vector.app.features.html.EventHtmlRenderer
import im.vector.app.features.html.PillsPostProcessor
import im.vector.app.features.html.SpanUtils
import im.vector.app.features.html.VectorHtmlCompressor
import im.vector.app.features.location.INITIAL_MAP_ZOOM_IN_TIMELINE
import im.vector.app.features.location.UrlMapProvider
import im.vector.app.features.location.toLocationData
import im.vector.app.features.media.ImageContentRenderer
import im.vector.app.features.media.VideoContentRenderer
import im.vector.app.features.poll.PollState
import im.vector.app.features.poll.PollState.Ended
import im.vector.app.features.poll.PollState.Ready
import im.vector.app.features.poll.PollState.Sending
import im.vector.app.features.poll.PollState.Undisclosed
import im.vector.app.features.poll.PollState.Voted
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.voice.AudioWaveformView
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import me.gujun.android.span.span
import org.matrix.android.sdk.api.MatrixUrls.isMxcUrl
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.attachments.toElementToDecrypt
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.api.session.events.model.content.EncryptedEventContent
import org.matrix.android.sdk.api.session.events.model.isThread
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.livelocation.LiveLocationBeaconContent
import org.matrix.android.sdk.api.session.room.model.message.MessageAudioContent
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageContentWithFormattedBody
import org.matrix.android.sdk.api.session.room.model.message.MessageCusAwardContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusChartContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusClusterContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusDaoContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusDposContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusDposOverContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusJoinContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusLordContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusPosContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusTxtContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusVoteContent
import org.matrix.android.sdk.api.session.room.model.message.MessageEmoteContent
import org.matrix.android.sdk.api.session.room.model.message.MessageFileContent
import org.matrix.android.sdk.api.session.room.model.message.MessageGiftsContent
import org.matrix.android.sdk.api.session.room.model.message.MessageImageInfoContent
import org.matrix.android.sdk.api.session.room.model.message.MessageLocationContent
import org.matrix.android.sdk.api.session.room.model.message.MessageNoticeContent
import org.matrix.android.sdk.api.session.room.model.message.MessagePollContent
import org.matrix.android.sdk.api.session.room.model.message.MessageRedPacketContent
import org.matrix.android.sdk.api.session.room.model.message.MessageTextContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.message.MessageVerificationRequestContent
import org.matrix.android.sdk.api.session.room.model.message.MessageVideoContent
import org.matrix.android.sdk.api.session.room.model.message.PollAnswer
import org.matrix.android.sdk.api.session.room.model.message.PollType
import org.matrix.android.sdk.api.session.room.model.message.getFileUrl
import org.matrix.android.sdk.api.session.room.model.message.getThumbnailUrl
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.settings.LightweightSettingsStorage
import org.matrix.android.sdk.api.util.MimeTypes
import timber.log.Timber
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

class MessageItemFactory @Inject constructor(
        private val localFilesHelper: LocalFilesHelper,
        private val colorProvider: ColorProvider,
        private val dimensionConverter: DimensionConverter,
        private val timelineMediaSizeProvider: TimelineMediaSizeProvider,
        private val htmlRenderer: Lazy<EventHtmlRenderer>,
        private val htmlCompressor: VectorHtmlCompressor,
        private val textRendererFactory: EventTextRenderer.Factory,
        private val stringProvider: StringProvider,
        private val imageContentRenderer: ImageContentRenderer,
        private val messageInformationDataFactory: MessageInformationDataFactory,
        private val messageItemAttributesFactory: MessageItemAttributesFactory,
        private val contentUploadStateTrackerBinder: ContentUploadStateTrackerBinder,
        private val contentDownloadStateTrackerBinder: ContentDownloadStateTrackerBinder,
        private val defaultItemFactory: DefaultItemFactory,
        private val noticeItemFactory: NoticeItemFactory,
        private val avatarSizeProvider: AvatarSizeProvider,
        private val pillsPostProcessorFactory: PillsPostProcessor.Factory,
        private val lightweightSettingsStorage: LightweightSettingsStorage,
        private val spanUtils: SpanUtils,
        private val session: Session,
        private val context: Context,
        private val audioMessagePlaybackTracker: AudioMessagePlaybackTracker,
        private val locationPinProvider: LocationPinProvider,
        private val vectorPreferences: VectorPreferences,
        private val urlMapProvider: UrlMapProvider,
        private val liveLocationMessageItemFactory: LiveLocationMessageItemFactory,
) {

    
    private var roomId: String = ""

    private val pillsPostProcessor by lazy {
        pillsPostProcessorFactory.create(roomId)
    }

    private val textRenderer by lazy {
        textRendererFactory.create(roomId)
    }

    fun create(params: TimelineItemFactoryParams): VectorEpoxyModel<*>? {
        val event = params.event
        val highlight = params.isHighlighted
        val callback = params.callback
        event.root.eventId ?: return null
        roomId = event.roomId
        val informationData = messageInformationDataFactory.create(params)
        val threadDetails = if (params.isFromThreadTimeline()) null else event.root.threadDetails

        if (event.root.isRedacted()) {
            
            val attributes = messageItemAttributesFactory.create(null, informationData, callback, params.reactionsSummaryEvents, threadDetails)
            return buildRedactedItem(attributes, highlight)
        }

        val messageContent = event.getLastMessageContent()
        if (messageContent == null) {
            val malformedText = stringProvider.getString(R.string.malformed_message)
            return defaultItemFactory.create(malformedText, informationData, highlight, callback)
        }

        if (messageContent.relatesTo?.type == RelationType.REPLACE || event.isEncrypted() && event.root.content.toModel<EncryptedEventContent>()?.relatesTo?.type == RelationType.REPLACE) {
            

            return noticeItemFactory.create(params)
        }

        if (lightweightSettingsStorage.areThreadMessagesEnabled() && !params.isFromThreadTimeline() && event.root.isThread()) {
            
            return noticeItemFactory.create(params)
        }

        
        val attributes = messageItemAttributesFactory.create(messageContent, informationData, callback, params.reactionsSummaryEvents, threadDetails)

        
        
        val messageItem = when (messageContent) {
            is MessageEmoteContent               -> buildEmoteMessageItem(messageContent, informationData, highlight, callback, attributes)
            is MessageTextContent                -> buildItemForTextContent(messageContent, informationData, highlight, callback, attributes)
            is MessageCusAwardContent            -> buildMessageCusAwardContent(params, messageContent, attributes)
            is MessageCusClusterContent          -> buildMessageCusClusterContent(params, attributes)
            is MessageCusDposContent             -> buildMessageCusDposContent(params, attributes)
            is MessageCusDposOverContent         -> buildMessageCusDposOverContent(attributes)
            is MessageCusLordContent             -> buildMessageCusLordContent(params, attributes)
            is MessageCusJoinContent             -> buildCusJoinItem(messageContent, attributes)
            
            is MessageCusTxtContent              -> buildCusTxtItem(messageContent, attributes)
            is MessageCusPosContent              -> buildCusPosItem(params, messageContent, attributes)
            is MessageCusVoteContent             -> buildCusVoteItem(params, messageContent, attributes)
            is MessageCusDaoContent              -> buildCusDaoItem(params, attributes)
            is MessageCusChartContent            -> buildCusChartItem(params, messageContent, attributes)
            
            is MessageImageInfoContent           -> buildImageMessageItem(messageContent, informationData, highlight, callback, attributes)
            is MessageNoticeContent              -> buildNoticeMessageItem(messageContent, informationData, highlight, callback, attributes)
            is MessageVideoContent               -> buildVideoMessageItem(messageContent, informationData, highlight, callback, attributes)
            is MessageFileContent                -> buildFileMessageItem(messageContent, highlight, attributes)
            is MessageAudioContent               -> buildAudioContent(params, messageContent, informationData, highlight, attributes)
            is MessageVerificationRequestContent -> buildVerificationRequestMessageItem(messageContent, informationData, highlight, callback, attributes)
            is MessagePollContent                -> buildPollItem(messageContent, informationData, highlight, callback, attributes)
            is MessageLocationContent            -> {
                if (vectorPreferences.labsRenderLocationsInTimeline()) {
                    buildLocationItem(messageContent, informationData, highlight, attributes)
                } else {
                    buildMessageTextItem(messageContent.body, false, informationData, highlight, callback, attributes)
                }
            }
            is MessageRedPacketContent           -> buildRedPacketItem(messageContent, attributes)
            is MessageGiftsContent               -> buildGiftsItem(messageContent, attributes)
            is LiveLocationBeaconContent         -> liveLocationMessageItemFactory.create(messageContent, highlight, attributes)
            else                                 -> {
                if (messageContent.msgType == MessageType.MSGTYPE_TEXT_WELCOME) {
                    buildCusWelcomeItem(attributes)
                } else if (messageContent.msgType == MessageType.MSGTYPE_TEXT_CREATE_CLUSTER) {
                    buildCusCreateClusterItem(attributes)
                } else {
                    buildNotHandledMessageItem(messageContent, informationData, highlight, callback, attributes)
                }
            }
        }


        return messageItem?.apply {
            layout(informationData.messageLayout.layoutRes)
        }
    }

    
    private fun buildCusCreateClusterItem(attributes: AbsMessageItem.Attributes): MessageCusJoinItem? {

        val title = if (attributes.informationData.izOwner) {
            stringProvider.getString(R.string.cuscreate_cluster_title)
        } else {
            stringProvider.getString(R.string.cuscreate_cluster_title_member)
        }
        val content = if (attributes.informationData.izOwner) {
            stringProvider.getString(R.string.cuscreate_cluster_message)
        } else {
            stringProvider.getString(R.string.cuscreate_cluster_message_member)
        }

        return MessageCusJoinItem_().attributes(attributes).title(title.toEpoxyCharSequence())
                
                .nickName(attributes.informationData.memberName?.toEpoxyCharSequence()).message(content.toEpoxyCharSequence())
    }

    
    private fun buildCusWelcomeItem(attributes: AbsMessageItem.Attributes): MessageCusWelcomeItem? {
        return MessageCusWelcomeItem_().attributes(attributes).title(stringProvider.getString(R.string.cus_welcom_title).toEpoxyCharSequence()).message(
                stringProvider.getString(R.string.cus_welcom_content).toEpoxyCharSequence()
        )
    }
    
    
    private fun buildCusTxtItem(messageContent: MessageCusTxtContent, attributes: AbsMessageItem.Attributes): MessageCusJoinItem? {
        val content = messageContent.content
        return MessageCusJoinItem_().attributes(attributes).type(messageContent.type).forHtml(messageContent.forHtml).title(messageContent.title.toEpoxyCharSequence()).nickName(
                messageContent.nickName.toEpoxyCharSequence()
        ).message(content.toEpoxyCharSequence())
    }
    
    
    private fun buildMessageCusAwardContent(
            params: TimelineItemFactoryParams,
            messageContent: MessageCusAwardContent,
            attributes: AbsMessageItem.Attributes,
    ): VectorEpoxyModel<*>? {
        val content = ""

        return MessageCusPledgeAwardItem_().attributes(attributes).status(messageContent.status).balance(messageContent.balance).type(messageContent.type).clickListener { _ ->
            params.callback?.onBalanceClicked(params.event)
        }.message(content.toEpoxyCharSequence())
    }

    
    private fun buildCusChartItem(params: TimelineItemFactoryParams, msg: MessageCusChartContent, attributes: AbsMessageItem.Attributes): MessageCusChartItem? {
        val content = ""
        return MessageCusChartItem_().attributes(attributes).deviceRate(msg.deviceRate.toEpoxyCharSequence()).deviceRateSmall(msg.deviceRateSmall.toEpoxyCharSequence()).deviceRateUp(
                msg.deviceRateUp
        ).connRate(msg.connRate.toEpoxyCharSequence()).connRateSmall(msg.connRateSmall.toEpoxyCharSequence()).connRateUp(msg.connRateUp).newDeviceNum(
                msg.newDeviceNum.toEpoxyCharSequence()
        ).dvmNum(msg.dvmNum.toEpoxyCharSequence()).posNum(msg.posNum.toEpoxyCharSequence()).day3Pos(msg.day3Pos.toEpoxyCharSequence()).day3Active(msg.day3Active.toEpoxyCharSequence()).day3Lg(
                msg.day3Lg.toEpoxyCharSequence()
        ).day7Pos(msg.day7Pos.toEpoxyCharSequence()).day7Active(msg.day7Active.toEpoxyCharSequence()).day7Lg(msg.day7Lg.toEpoxyCharSequence()).message(
                content.toEpoxyCharSequence()
        ).posClickListener { view ->
            params.callback?.posClickListener(view.tag as Boolean)
        }.activeClickListener { view ->
            params.callback?.posClickListener(view.tag as Boolean)
        }.lGClickListener { view ->
            params.callback?.posClickListener(view.tag as Boolean)
        }
    }

    
    private fun buildCusPosItem(params: TimelineItemFactoryParams, messageContent: MessageCusPosContent, attributes: AbsMessageItem.Attributes): MessageCusPosItem? {
        return MessageCusPosItem_().attributes(attributes).title(messageContent.title.toEpoxyCharSequence()).btStr(messageContent.btStr.toEpoxyCharSequence()).message(
                messageContent.body.toEpoxyCharSequence()
        ).OnClickListener { _ ->
            params.callback?.onPosClicked(messageContent.type, messageContent.param)
        }
    }

    
    private fun buildCusVoteItem(params: TimelineItemFactoryParams, messageContent: MessageCusVoteContent, attributes: AbsMessageItem.Attributes): MessageCusVoteItem? {
        return MessageCusVoteItem_().attributes(attributes).title(messageContent.title.toEpoxyCharSequence()).time(messageContent.time.toEpoxyCharSequence()).OnClickListener { _ ->
            params.callback?.onVoteClicked()
        }
    }

    
    private fun buildCusDaoItem(params: TimelineItemFactoryParams, attributes: AbsMessageItem.Attributes): MessageCusDaoItem? {
        return MessageCusDaoItem_().attributes(attributes).OnClickListener { _ ->
            params.callback?.onDaoClicked()
        }
    }

    private fun buildRedPacketItem(redpacketContent: MessageRedPacketContent, attributes: AbsMessageItem.Attributes): MessageRedPacketItem? {
        return MessageRedPacketItem_().attributes(attributes).redPacketId(redpacketContent.redPacketId).transferNum("${redpacketContent.transferNum} ${redpacketContent.transferSymbol}")
    }

    
    private fun buildMessageCusClusterContent(
            params: TimelineItemFactoryParams,
            attributes: AbsMessageItem.Attributes,
    ): VectorEpoxyModel<*>? {
        return MessageCusClusterItem_().attributes(attributes).clickListener {
            params.callback?.onCusClusterClicked()
        }.message("".toEpoxyCharSequence())
    }

    private fun buildMessageCusDposContent(
            params: TimelineItemFactoryParams,
            attributes: AbsMessageItem.Attributes,
    ): VectorEpoxyModel<*>? {
        return MessageCusDposItem_().attributes(attributes).dposToClickListener {
            params.callback?.onDposToClicked()
        }.dposMethodClickListener {
            params.callback?.onDposMethodClicked()
        }.message("".toEpoxyCharSequence())
    }

    private fun buildMessageCusDposOverContent(
            attributes: AbsMessageItem.Attributes,
    ): VectorEpoxyModel<*>? {

        return MessageCusDposOverItem_().attributes(attributes).message("".toEpoxyCharSequence())
    }

    private fun buildMessageCusLordContent(
            params: TimelineItemFactoryParams,
            attributes: AbsMessageItem.Attributes,
    ): VectorEpoxyModel<*>? {
        return MessageCusLordItem_().attributes(attributes).invitateClickListener {
            params.callback?.onLordInvitateClicked()
        }.manageClickListener {
            params.callback?.onLordManageClicked()
        }.message("".toEpoxyCharSequence())
    }

    private fun buildCusJoinItem(
            messageContent: MessageCusJoinContent,
            attributes: AbsMessageItem.Attributes,
    ): VectorEpoxyModel<*>? {
        return MessageCusJoinItem_().attributes(attributes).message(messageContent.body.toEpoxyCharSequence())
    }

    
    private fun buildGiftsItem(giftsContent: MessageGiftsContent, attributes: AbsMessageItem.Attributes): MessageGiftsItem? {

        val data = context.getString(R.string.gifts_content_tips);
        val formattedBody = SpannableStringBuilder(data)
        formattedBody.setSpan(AbsoluteSizeSpan(12, true), 0, data.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        for (index in 0..giftsContent.giftsNum) {
            formattedBody.append(context.getString(R.string.gifts_content_flower))
        }
        val res: Resources = context.getResources()
        
        val imgRes: Int = R.drawable.gifts_flowers
        
        val dp32 = dimensionConverter.dpToPx(32)
        val size: Int = (dp32 * 66.0 / 70).toInt()

        
        val bitmap = BitmapFactory.decodeResource(res, imgRes)
        val scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, dp32, true)

        val regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]"
        val patternEmotion = Pattern.compile(regexEmotion)
        val matcherEmotion = patternEmotion.matcher(formattedBody)
        while (matcherEmotion.find()) {
            val span = ImageSpan(context, scaleBitmap)
            
            val key = matcherEmotion.group()
            
            val start = matcherEmotion.start()
            formattedBody.setSpan(span, start, start + key.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }


        return MessageGiftsItem_().attributes(attributes)
                .message(formattedBody.toEpoxyCharSequence())
    }

    private fun buildLocationItem(
            locationContent: MessageLocationContent,
            informationData: MessageInformationData,
            highlight: Boolean,
            attributes: AbsMessageItem.Attributes,
    ): MessageLocationItem? {
        val width = timelineMediaSizeProvider.getMaxSize().first
        val height = dimensionConverter.dpToPx(MESSAGE_LOCATION_ITEM_HEIGHT_IN_DP)

        val locationUrl = locationContent.toLocationData()?.let {
            urlMapProvider.buildStaticMapUrl(it, INITIAL_MAP_ZOOM_IN_TIMELINE, width, height)
        }

        val userId = if (locationContent.isSelfLocation()) informationData.senderId else null

        return MessageLocationItem_().attributes(attributes).locationUrl(locationUrl).mapWidth(width).mapHeight(height).userId(userId).locationPinProvider(
                locationPinProvider
        ).highlighted(highlight).leftGuideline(avatarSizeProvider.leftGuideline)
    }

    private fun buildPollItem(
            pollContent: MessagePollContent,
            informationData: MessageInformationData,
            highlight: Boolean,
            callback: TimelineEventController.Callback?,
            attributes: AbsMessageItem.Attributes,
    ): PollItem {
        val pollResponseSummary = informationData.pollResponseAggregatedSummary
        val pollState = createPollState(informationData, pollResponseSummary, pollContent)
        val pollCreationInfo = pollContent.getBestPollCreationInfo()
        val questionText = pollCreationInfo?.question?.getBestQuestion().orEmpty()
        val question = createPollQuestion(informationData, questionText, callback)
        val optionViewStates = pollCreationInfo?.answers?.mapToOptions(pollState, informationData)
        val totalVotesText = createTotalVotesText(pollState, pollResponseSummary)

        return PollItem_().attributes(attributes).eventId(informationData.eventId).pollQuestion(question).canVote(pollState.isVotable()).totalVotesText(
                totalVotesText
        ).optionViewStates(optionViewStates).edited(informationData.hasBeenEdited).highlighted(highlight).leftGuideline(avatarSizeProvider.leftGuideline).callback(
                callback
        )
    }

    private fun createPollState(
            informationData: MessageInformationData,
            pollResponseSummary: PollResponseData?,
            pollContent: MessagePollContent,
    ): PollState = when {
        !informationData.sendState.isSent()                                 -> Sending
        pollResponseSummary?.isClosed.orFalse()                             -> Ended
        pollContent.getBestPollCreationInfo()?.kind == PollType.UNDISCLOSED -> Undisclosed
        pollResponseSummary?.myVote?.isNotEmpty().orFalse()                 -> Voted(pollResponseSummary?.totalVotes ?: 0)
        else                                                                -> Ready
    }

    private fun List<PollAnswer>.mapToOptions(
            pollState: PollState,
            informationData: MessageInformationData,
    ) = map { answer ->
        val pollResponseSummary = informationData.pollResponseAggregatedSummary
        val winnerVoteCount = pollResponseSummary?.winnerVoteCount
        val optionId = answer.id ?: ""
        val optionAnswer = answer.getBestAnswer() ?: ""
        val voteSummary = pollResponseSummary?.votes?.get(answer.id)
        val voteCount = voteSummary?.total ?: 0
        val votePercentage = voteSummary?.percentage ?: 0.0
        val isMyVote = pollResponseSummary?.myVote == answer.id
        val isWinner = winnerVoteCount != 0 && voteCount == winnerVoteCount

        when (pollState) {
            Sending     -> PollSending(optionId, optionAnswer)
            Ready       -> PollReady(optionId, optionAnswer)
            is Voted    -> PollVoted(optionId, optionAnswer, voteCount, votePercentage, isMyVote)
            Undisclosed -> PollUndisclosed(optionId, optionAnswer, isMyVote)
            Ended       -> PollEnded(optionId, optionAnswer, voteCount, votePercentage, isWinner)
        }
    }

    private fun createPollQuestion(
            informationData: MessageInformationData,
            question: String,
            callback: TimelineEventController.Callback?,
    ) = if (informationData.hasBeenEdited) {
        annotateWithEdited(question, callback, informationData)
    } else {
        question
    }.toEpoxyCharSequence()

    private fun createTotalVotesText(
            pollState: PollState,
            pollResponseSummary: PollResponseData?,
    ): String {
        val votes = pollResponseSummary?.totalVotes ?: 0
        return when {
            pollState is Ended       -> stringProvider.getQuantityString(R.plurals.poll_total_vote_count_after_ended, votes, votes)
            pollState is Undisclosed -> ""
            pollState is Voted       -> stringProvider.getQuantityString(R.plurals.poll_total_vote_count_before_ended_and_voted, votes, votes)
            votes == 0               -> stringProvider.getString(R.string.poll_no_votes_cast)
            else                     -> stringProvider.getQuantityString(R.plurals.poll_total_vote_count_before_ended_and_not_voted, votes, votes)
        }
    }

    private fun buildAudioMessageItem(params: TimelineItemFactoryParams, messageContent: MessageAudioContent, informationData: MessageInformationData, highlight: Boolean, attributes: AbsMessageItem.Attributes): MessageAudioItem {
        val fileUrl = getAudioFileUrl(messageContent, informationData)
        val playbackControlButtonClickListener = createOnPlaybackButtonClickListener(messageContent, informationData, params)
        val duration = messageContent.audioInfo?.duration ?: 0

        return MessageAudioItem_().attributes(attributes).filename(messageContent.body).duration(
                messageContent.audioInfo?.duration ?: 0
        ).playbackControlButtonClickListener(playbackControlButtonClickListener).audioMessagePlaybackTracker(audioMessagePlaybackTracker).isLocalFile(
                localFilesHelper.isLocalFile(fileUrl)
        ).fileSize(messageContent.audioInfo?.size ?: 0L).onSeek {
            params.callback?.onAudioSeekBarMovedTo(
                    informationData.eventId, duration, it
            )
        }.mxcUrl(fileUrl).contentUploadStateTrackerBinder(contentUploadStateTrackerBinder).contentDownloadStateTrackerBinder(
                contentDownloadStateTrackerBinder
        ).highlighted(highlight).leftGuideline(avatarSizeProvider.leftGuideline)
    }

    private fun getAudioFileUrl(
            messageContent: MessageAudioContent,
            informationData: MessageInformationData,
    ) = messageContent.getFileUrl()?.let {
        if (informationData.sentByMe && !informationData.sendState.isSent()) {
            it
        } else {
            it.takeIf { it.isMxcUrl() }
        }
    } ?: ""

    private fun createOnPlaybackButtonClickListener(
            messageContent: MessageAudioContent,
            informationData: MessageInformationData,
            params: TimelineItemFactoryParams,
    ) = object : ClickListener {
        override fun invoke(view: View) {
            if (informationData.sendStateDecoration == SendStateDecoration.SENT) {
                
            }
            params.callback?.onVoiceControlButtonClicked(informationData.eventId, messageContent)
        }
    }

    private fun buildVoiceMessageItem(params: TimelineItemFactoryParams, messageContent: MessageAudioContent, informationData: MessageInformationData, highlight: Boolean, attributes: AbsMessageItem.Attributes): MessageVoiceItem {
        val fileUrl = getAudioFileUrl(messageContent, informationData)
        val playbackControlButtonClickListener = createOnPlaybackButtonClickListener(messageContent, informationData, params)

        val waveformTouchListener: MessageVoiceItem.WaveformTouchListener = object : MessageVoiceItem.WaveformTouchListener {
            override fun onWaveformTouchedUp(percentage: Float) {
                val duration = messageContent.audioInfo?.duration ?: 0
                params.callback?.onVoiceWaveformTouchedUp(informationData.eventId, duration, percentage)
            }

            override fun onWaveformMovedTo(percentage: Float) {
                val duration = messageContent.audioInfo?.duration ?: 0
                params.callback?.onVoiceWaveformMovedTo(informationData.eventId, duration, percentage)
            }
        }

        return MessageVoiceItem_().attributes(attributes).duration(
                messageContent.audioWaveformInfo?.duration ?: 0
        ).waveform(messageContent.audioWaveformInfo?.waveform?.toFft().orEmpty()).playbackControlButtonClickListener(playbackControlButtonClickListener).waveformTouchListener(
                waveformTouchListener
        ).audioMessagePlaybackTracker(audioMessagePlaybackTracker).isLocalFile(localFilesHelper.isLocalFile(fileUrl)).mxcUrl(fileUrl).contentUploadStateTrackerBinder(
                contentUploadStateTrackerBinder
        ).contentDownloadStateTrackerBinder(contentDownloadStateTrackerBinder).highlighted(highlight).leftGuideline(avatarSizeProvider.leftGuideline)
    }

    private fun buildVoiceCusMessageItem(params: TimelineItemFactoryParams, messageContent: MessageAudioContent, informationData: MessageInformationData, highlight: Boolean, attributes: AbsMessageItem.Attributes): MessageCusVoiceItem {
        val fileUrl = getAudioFileUrl(messageContent, informationData)

        val playbackControlButtonClickListener = createOnPlaybackButtonClickListener(messageContent, informationData, params)

        val waveformTouchListener: MessageCusVoiceItem.WaveformTouchListener = object : MessageCusVoiceItem.WaveformTouchListener {
            override fun onWaveformTouchedUp(percentage: Float) {
                val duration = messageContent.audioInfo?.duration ?: 0
                params.callback?.onVoiceWaveformTouchedUp(informationData.eventId, duration, percentage)
            }

            override fun onWaveformMovedTo(percentage: Float) {
                val duration = messageContent.audioInfo?.duration ?: 0
                params.callback?.onVoiceWaveformMovedTo(informationData.eventId, duration, percentage)
            }
        }
        val originText = messageContent.audioInfo?.originText
        val translate = messageContent.audioInfo?.translate
        Timber.i("-------MessageItemFactory------originText:$originText----buildVoiceCusMessageItem----------------$fileUrl----------")
        
        val layout = attributes.informationData.messageLayout

        val copy = if (layout is TimelineMessageLayout.Bubble) {
            attributes.copy(
                    informationData = attributes.informationData.copy(
                            messageLayout = layout.copy(showTimestamp = false)
                    )
            )
        } else {
            attributes
        }
        return MessageCusVoiceItem_().attributes(copy).originText(originText).translate(translate)
                .step(messageContent.step)
                .duration(
                        messageContent.audioWaveformInfo?.duration ?: 0
                ).waveform(messageContent.audioWaveformInfo?.waveform?.toFft().orEmpty()).playbackControlButtonClickListener(playbackControlButtonClickListener).waveformTouchListener(
                        waveformTouchListener
                ).audioMessagePlaybackTracker(audioMessagePlaybackTracker).isLocalFile(localFilesHelper.isLocalFile(fileUrl)).mxcUrl(fileUrl).contentUploadStateTrackerBinder(
                        contentUploadStateTrackerBinder
                ).contentDownloadStateTrackerBinder(contentDownloadStateTrackerBinder).highlighted(highlight).leftGuideline(avatarSizeProvider.leftGuideline)
    }

    private fun buildVerificationRequestMessageItem(
            messageContent: MessageVerificationRequestContent,
            @Suppress("UNUSED_PARAMETER") informationData: MessageInformationData,
            highlight: Boolean,
            callback: TimelineEventController.Callback?,
            attributes: AbsMessageItem.Attributes,
    ): VerificationRequestItem? {
        
        val myUserId = session.myUserId
        if (informationData.senderId != myUserId && messageContent.toUserId != myUserId) {
            return null
        }

        val otherUserId = if (informationData.sentByMe) messageContent.toUserId else informationData.senderId
        val otherUserName = if (informationData.sentByMe) {
            session.getRoomMember(messageContent.toUserId, roomId)?.displayName
        } else {
            informationData.memberName
        }
        return VerificationRequestItem_().attributes(
                VerificationRequestItem.Attributes(
                        otherUserId = otherUserId,
                        otherUserName = otherUserName.toString(),
                        referenceId = informationData.eventId,
                        informationData = informationData,
                        avatarRenderer = attributes.avatarRenderer,
                        messageColorProvider = attributes.messageColorProvider,
                        itemLongClickListener = attributes.itemLongClickListener,
                        itemClickListener = attributes.itemClickListener,
                        reactionPillCallback = attributes.reactionPillCallback,
                        readReceiptsCallback = attributes.readReceiptsCallback,
                        emojiTypeFace = attributes.emojiTypeFace,
                        reactionsSummaryEvents = attributes.reactionsSummaryEvents,
                )
        ).callback(callback).highlighted(highlight).leftGuideline(avatarSizeProvider.leftGuideline)
    }

    private fun buildFileMessageItem(
            messageContent: MessageFileContent,
            highlight: Boolean,
            attributes: AbsMessageItem.Attributes,
    ): MessageFileItem {
        val mxcUrl = messageContent.getFileUrl() ?: ""
        return MessageFileItem_().attributes(attributes).leftGuideline(avatarSizeProvider.leftGuideline).isLocalFile(localFilesHelper.isLocalFile(messageContent.getFileUrl())).isDownloaded(
                session.fileService().isFileInCache(messageContent)
        ).mxcUrl(mxcUrl).contentUploadStateTrackerBinder(contentUploadStateTrackerBinder).contentDownloadStateTrackerBinder(
                contentDownloadStateTrackerBinder
        ).highlighted(highlight).filename(messageContent.body).iconRes(R.drawable.ic_paperclip)
    }

    private fun buildAudioContent(
            params: TimelineItemFactoryParams,
            messageContent: MessageAudioContent,
            informationData: MessageInformationData,
            highlight: Boolean,
            attributes: AbsMessageItem.Attributes,
    ) = if (messageContent.voiceMessageIndicator != null) {

        val originText = messageContent.audioInfo?.originText
        val translate = messageContent.audioInfo?.translate
        if (originText.isNullOrEmpty() && translate.isNullOrEmpty()) {
            buildVoiceMessageItem(params, messageContent, informationData, highlight, attributes)
        } else {
            buildVoiceCusMessageItem(params, messageContent, informationData, highlight, attributes)
        }
    } else {
        buildAudioMessageItem(params, messageContent, informationData, highlight, attributes)
    }

    private fun buildNotHandledMessageItem(messageContent: MessageContent, informationData: MessageInformationData, highlight: Boolean, callback: TimelineEventController.Callback?, attributes: AbsMessageItem.Attributes): MessageTextItem? {
        
        return buildMessageTextItem(messageContent.body, false, informationData, highlight, callback, attributes)
    }

    private fun buildImageMessageItem(
            messageContent: MessageImageInfoContent,
            @Suppress("UNUSED_PARAMETER") informationData: MessageInformationData,
            highlight: Boolean,
            callback: TimelineEventController.Callback?,
            attributes: AbsMessageItem.Attributes,
    ): MessageImageVideoItem? {
        val (maxWidth, maxHeight) = timelineMediaSizeProvider.getMaxSize()
        val data = ImageContentRenderer.Data(
                eventId = informationData.eventId,
                filename = messageContent.body,
                mimeType = messageContent.mimeType,
                url = messageContent.getFileUrl(),
                elementToDecrypt = messageContent.encryptedFileInfo?.toElementToDecrypt(),
                height = messageContent.info?.height,
                maxHeight = maxHeight,
                width = messageContent.info?.width,
                maxWidth = maxWidth,
                allowNonMxcUrls = informationData.sendState.isSending()
        )
        return MessageImageVideoItem_().attributes(attributes).leftGuideline(avatarSizeProvider.leftGuideline).imageContentRenderer(imageContentRenderer).contentUploadStateTrackerBinder(
                contentUploadStateTrackerBinder
        ).playable(messageContent.mimeType == MimeTypes.Gif).highlighted(highlight).mediaData(data).apply {
            if (messageContent.msgType == MessageType.MSGTYPE_STICKER_LOCAL) {
                mode(ImageContentRenderer.Mode.STICKER)
                clickListener { view ->
                    callback?.onImageMessageClicked(messageContent, data, view, listOf(data))
                }
            } else {
                clickListener { view ->
                    callback?.onImageMessageClicked(messageContent, data, view, emptyList())
                }
            }
        }
    }

    private fun buildVideoMessageItem(
            messageContent: MessageVideoContent,
            informationData: MessageInformationData,
            highlight: Boolean,
            callback: TimelineEventController.Callback?,
            attributes: AbsMessageItem.Attributes,
    ): MessageImageVideoItem? {
        val (maxWidth, maxHeight) = timelineMediaSizeProvider.getMaxSize()
        val thumbnailData = ImageContentRenderer.Data(
                eventId = informationData.eventId,
                filename = messageContent.body,
                mimeType = messageContent.mimeType,
                url = messageContent.videoInfo?.getThumbnailUrl(),
                elementToDecrypt = messageContent.videoInfo?.thumbnailFile?.toElementToDecrypt(),
                height = messageContent.videoInfo?.height,
                maxHeight = maxHeight,
                width = messageContent.videoInfo?.width,
                maxWidth = maxWidth,
                allowNonMxcUrls = informationData.sendState.isSending()
        )

        val videoData = VideoContentRenderer.Data(
                eventId = informationData.eventId,
                filename = messageContent.body,
                mimeType = messageContent.mimeType,
                url = messageContent.getFileUrl(),
                elementToDecrypt = messageContent.encryptedFileInfo?.toElementToDecrypt(),
                thumbnailMediaData = thumbnailData
        )

        return MessageImageVideoItem_().leftGuideline(avatarSizeProvider.leftGuideline).attributes(attributes).imageContentRenderer(imageContentRenderer).contentUploadStateTrackerBinder(
                contentUploadStateTrackerBinder
        ).playable(true).highlighted(highlight).mediaData(thumbnailData).clickListener { view ->
            callback?.onVideoMessageClicked(
                    messageContent, videoData, view.findViewById(R.id.messageThumbnailView)
            )
        }
    }

    private fun buildItemForTextContent(
            messageContent: MessageTextContent,
            informationData: MessageInformationData,
            highlight: Boolean,
            callback: TimelineEventController.Callback?,
            attributes: AbsMessageItem.Attributes,
    ): VectorEpoxyModel<*>? {
        val matrixFormattedBody = messageContent.matrixFormattedBody

        return if (matrixFormattedBody != null) {
            buildFormattedTextItem(matrixFormattedBody, informationData, highlight, callback, attributes)
        } else {
            buildMessageTextItem(messageContent.body, false, informationData, highlight, callback, attributes)
        }
    }

    var mPattern = Pattern.compile("<mx-reply><a href=.*>In reply to</a> <a href=.*>.*</a><br/>.*</mx-reply>")
    var mPattern2 = Pattern.compile("</a><br/>.*</mx-reply>")

    private fun buildFormattedTextItem(
            matrixFormattedBody: String,
            informationData: MessageInformationData,
            highlight: Boolean,
            callback: TimelineEventController.Callback?,
            attributes: AbsMessageItem.Attributes,
    ): MessageTextItem? {

        val compressed = htmlCompressor.compress(matrixFormattedBody)

        var temp = compressed
        var renderedFormattedBody: Spanned

        Timber.i("-----buildFormattedTextItem----matrixFormattedBody:${matrixFormattedBody}----------------------MyPledge---------")
        try {
            val m: Matcher = mPattern.matcher(temp)

            
            val find = if (m.find()) {
                m.group();
            } else {
                ""
            }
            val matcher = mPattern2.matcher(find)
            var find2 = if (matcher.find()) {
                matcher.group();
            } else {
                ""
            }
            find2 = find2.replace("</a><br/>", "")
            find2 = find2.replace("</mx-reply>", "")

            
            val content = compressed.substring(find.length)
            val replace = find.replace("In reply to", context.getString(R.string.reply_to))

            temp = replace + content

            renderedFormattedBody = htmlRenderer.get().render(temp, pillsPostProcessor) as Spanned
            val find2RenderedFormattedBody = htmlRenderer.get().render(find2, pillsPostProcessor) as Spanned
            val span = AbsoluteSizeSpan(dimensionConverter.dpToPx(12))
            val builder = SpannableStringBuilder(renderedFormattedBody)

            val find2RenderedString = find2RenderedFormattedBody.toString()
            
            val indexOf = builder.indexOf(find2RenderedString)
            val lastIndexOf = builder.indexOf(find2RenderedString) + find2RenderedString.length


            builder.setSpan(span, indexOf, lastIndexOf, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            renderedFormattedBody = builder
        } catch (e: Exception) {
            e.printStackTrace()
            renderedFormattedBody = htmlRenderer.get().render(compressed, pillsPostProcessor) as Spanned
        }


        return buildMessageTextItem(renderedFormattedBody, true, informationData, highlight, callback, attributes)
    }

    private fun buildMessageTextItem(
            body: CharSequence,
            isFormatted: Boolean,
            informationData: MessageInformationData,
            highlight: Boolean,
            callback: TimelineEventController.Callback?,
            attributes: AbsMessageItem.Attributes,
    ): MessageTextItem? {
        val renderedBody = textRenderer.render(body)

        val bindingOptions = spanUtils.getBindingOptions(renderedBody)
        val linkifiedBody = renderedBody.linkify(callback)

        return MessageTextItem_().message(
                if (informationData.hasBeenEdited) {
                    annotateWithEdited(linkifiedBody, callback, informationData)
                } else {
                    linkifiedBody
                }.toEpoxyCharSequence()
        ).useBigFont(linkifiedBody.length <= MAX_NUMBER_OF_EMOJI_FOR_BIG_FONT * 2 && containsOnlyEmojis(linkifiedBody.toString())).bindingOptions(
                bindingOptions
        ).markwonPlugins(htmlRenderer.get().plugins).searchForPills(isFormatted).previewUrlRetriever(callback?.getPreviewUrlRetriever()).imageContentRenderer(
                imageContentRenderer
        ).previewUrlCallback(callback).leftGuideline(avatarSizeProvider.leftGuideline).attributes(attributes).highlighted(highlight).movementMethod(
                createLinkMovementMethod(callback)
        )
    }

    private fun annotateWithEdited(
            linkifiedBody: CharSequence,
            callback: TimelineEventController.Callback?,
            informationData: MessageInformationData,
    ): Spannable {
        val spannable = SpannableStringBuilder()
        spannable.append(linkifiedBody)
        val editedSuffix = stringProvider.getString(R.string.edited_suffix)
        spannable.append(" ").append(editedSuffix)
        val color = colorProvider.getColorFromAttribute(R.attr.vctr_content_secondary)
        val editStart = spannable.lastIndexOf(editedSuffix)
        val editEnd = editStart + editedSuffix.length
        spannable.setSpan(
                ForegroundColorSpan(color), editStart, editEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )

        
        spannable.setSpan(
                AbsoluteSizeSpan(dimensionConverter.spToPx(13)), editStart, editEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        callback?.onEditedDecorationClicked(informationData)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        
                    }
                }, editStart, editEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private fun buildNoticeMessageItem(
            messageContent: MessageNoticeContent,
            @Suppress("UNUSED_PARAMETER") informationData: MessageInformationData,
            highlight: Boolean,
            callback: TimelineEventController.Callback?,
            attributes: AbsMessageItem.Attributes,
    ): MessageTextItem? {
        val htmlBody = messageContent.getHtmlBody()
        val formattedBody = span {
            text = htmlBody
            textColor = colorProvider.getColorFromAttribute(R.attr.vctr_content_secondary)
            textStyle = "italic"
        }

        val bindingOptions = spanUtils.getBindingOptions(htmlBody)
        val message = formattedBody.linkify(callback)

        return MessageTextItem_().leftGuideline(avatarSizeProvider.leftGuideline).previewUrlRetriever(callback?.getPreviewUrlRetriever()).imageContentRenderer(
                imageContentRenderer
        ).previewUrlCallback(callback).attributes(attributes).message(message.toEpoxyCharSequence()).bindingOptions(bindingOptions).highlighted(
                highlight
        ).movementMethod(createLinkMovementMethod(callback))
    }

    private fun buildEmoteMessageItem(
            messageContent: MessageEmoteContent,
            informationData: MessageInformationData,
            highlight: Boolean,
            callback: TimelineEventController.Callback?,
            attributes: AbsMessageItem.Attributes,
    ): MessageTextItem? {
        val formattedBody = SpannableStringBuilder()
        formattedBody.append("* ${informationData.memberName} ")
        formattedBody.append(messageContent.getHtmlBody())
        val bindingOptions = spanUtils.getBindingOptions(formattedBody)
        val message = formattedBody.linkify(callback)

        return MessageTextItem_().message(
                if (informationData.hasBeenEdited) {
                    annotateWithEdited(message, callback, informationData)
                } else {
                    message
                }.toEpoxyCharSequence()
        ).bindingOptions(bindingOptions).leftGuideline(avatarSizeProvider.leftGuideline).previewUrlRetriever(callback?.getPreviewUrlRetriever()).imageContentRenderer(
                imageContentRenderer
        ).previewUrlCallback(callback).attributes(attributes).highlighted(highlight).movementMethod(createLinkMovementMethod(callback))
    }

    private fun MessageContentWithFormattedBody.getHtmlBody(): CharSequence {
        return matrixFormattedBody?.let { htmlCompressor.compress(it) }?.let { htmlRenderer.get().render(it, pillsPostProcessor) } ?: body
    }

    private fun buildRedactedItem(
            attributes: AbsMessageItem.Attributes,
            highlight: Boolean,
    ): RedactedMessageItem? {
        return RedactedMessageItem_().layout(attributes.informationData.messageLayout.layoutRes).leftGuideline(avatarSizeProvider.leftGuideline).attributes(
                attributes
        ).highlighted(highlight)
    }

    private fun List<Int?>?.toFft(): List<Int>? {
        return this?.filterNotNull()?.map {
            
            it * AudioWaveformView.MAX_FFT / 1024
        }
    }

    companion object {
        private const val MAX_NUMBER_OF_EMOJI_FOR_BIG_FONT = 5
        const val MESSAGE_LOCATION_ITEM_HEIGHT_IN_DP = 200
    }
}
