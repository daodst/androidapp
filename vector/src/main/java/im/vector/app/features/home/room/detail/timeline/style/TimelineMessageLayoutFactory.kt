

package im.vector.app.features.home.room.detail.timeline.style

import android.content.res.Resources
import im.vector.app.R
import im.vector.app.core.extensions.localDateTime
import im.vector.app.core.resources.LocaleProvider
import im.vector.app.core.resources.isRTL
import im.vector.app.features.home.room.detail.timeline.factory.TimelineItemFactoryParams
import im.vector.app.features.settings.VectorPreferences
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.message.MessageVerificationRequestContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.room.timeline.isCus
import org.matrix.android.sdk.api.session.room.timeline.isEdition
import org.matrix.android.sdk.api.session.room.timeline.isLocal
import org.matrix.android.sdk.api.session.room.timeline.isRootThread
import javax.inject.Inject

class TimelineMessageLayoutFactory @Inject constructor(private val session: Session,
                                                       private val layoutSettingsProvider: TimelineLayoutSettingsProvider,
                                                       private val localeProvider: LocaleProvider,
                                                       private val resources: Resources,
                                                       private val vectorPreferences: VectorPreferences) {

    companion object {
        
        private val EVENT_TYPES_WITH_BUBBLE_LAYOUT = setOf(
                EventType.MESSAGE,
                EventType.MESSAGE_LOCAL,
                EventType.ENCRYPTED,
                EventType.STICKER
        ) + EventType.POLL_START + EventType.STATE_ROOM_BEACON_INFO

        
        private val MSG_TYPES_WITHOUT_BUBBLE_LAYOUT = setOf(
                MessageType.MSGTYPE_VERIFICATION_REQUEST
        )

        
        private val MSG_TYPES_WITH_PSEUDO_BUBBLE_LAYOUT = setOf(
                MessageType.MSGTYPE_IMAGE,
                MessageType.MSGTYPE_VIDEO,
                MessageType.MSGTYPE_RED_PACKET,
                MessageType.MSGTYPE_STICKER_LOCAL,
                MessageType.MSGTYPE_EMOTE,
                MessageType.MSGTYPE_LIVE_LOCATION_STATE,
        )
        private val MSG_TYPES_WITH_TIMESTAMP_INSIDE_MESSAGE = setOf(
                MessageType.MSGTYPE_IMAGE,
                MessageType.MSGTYPE_RED_PACKET,
                MessageType.MSGTYPE_VIDEO,
                MessageType.MSGTYPE_LIVE_LOCATION_STATE,
        )
    }

    private val cornerRadius: Float by lazy {
        resources.getDimensionPixelSize(R.dimen.chat_bubble_corner_radius).toFloat()
    }
    private val zGroupCornerRadius: Float by lazy {
        resources.getDimensionPixelSize(R.dimen.group_chat_bubble_corner_radius).toFloat()
    }
    private val isRTL: Boolean by lazy {
        localeProvider.isRTL()
    }

    fun create(params: TimelineItemFactoryParams): TimelineMessageLayout {
        val event = params.event
        val nextDisplayableEvent = params.nextDisplayableEvent
        val prevDisplayableEvent = params.prevDisplayableEvent
        val isSentByMe = event.root.senderId == session.myUserId

        val date = event.root.localDateTime()
        val nextDate = nextDisplayableEvent?.root?.localDateTime()
        val addDaySeparator = date.toLocalDate() != nextDate?.toLocalDate()

        val isNextMessageReceivedMoreThanOneHourAgo = nextDate?.isBefore(date.minusMinutes(60))
                ?: false

        val showInformation = addDaySeparator ||
                event.senderInfo.avatarUrl != nextDisplayableEvent?.senderInfo?.avatarUrl ||
                event.senderInfo.disambiguatedDisplayName != nextDisplayableEvent?.senderInfo?.disambiguatedDisplayName ||
                nextDisplayableEvent.root.getClearType() !in listOf(EventType.MESSAGE, EventType.MESSAGE_LOCAL, EventType.STICKER, EventType.ENCRYPTED) ||
                isNextMessageReceivedMoreThanOneHourAgo ||
                isTileTypeMessage(nextDisplayableEvent) ||
                event.isRootThread() ||
                nextDisplayableEvent.isEdition()

        val messageLayout = when (layoutSettingsProvider.getLayoutSettings()) {
            TimelineLayoutSettings.MODERN -> {
                buildModernLayout(showInformation)
            }
            TimelineLayoutSettings.BUBBLE -> {
                val shouldBuildBubbleLayout = event.shouldBuildBubbleLayout()
                if (shouldBuildBubbleLayout) {
                    val isFirstFromThisSender = nextDisplayableEvent == null || !nextDisplayableEvent.shouldBuildBubbleLayout() ||
                            nextDisplayableEvent.root.senderId != event.root.senderId || addDaySeparator

                    val isLastFromThisSender = prevDisplayableEvent == null || !prevDisplayableEvent.shouldBuildBubbleLayout() ||
                            prevDisplayableEvent.root.senderId != event.root.senderId ||
                            prevDisplayableEvent.root.localDateTime().toLocalDate() != date.toLocalDate()
                    val messageContent = event.getLastMessageContent()
                    val cornersRadius = buildCornersRadius(
                            izGroup = params.izGroup,
                            isIncoming = !isSentByMe || messageContent.isLocal(),
                            isFirstFromThisSender = isFirstFromThisSender || messageContent.isLocal(),
                            isLastFromThisSender = isLastFromThisSender || messageContent.isLocal()
                    )

                    val isCus = messageContent.isCus()

                    val isNextCus = nextDisplayableEvent?.getLastMessageContent()?.isCus() ?: false

                    val isIncoming = !isSentByMe || messageContent.isLocal()
                    TimelineMessageLayout.Bubble(
                            izGroup = params.izGroup,
                            
                            isCus = isCus,
                            
                            isLocal = messageContent.isLocal(),
                            
                            showTimestamp = !messageContent.isCus(),
                            
                            showAvatar = showInformation,
                            showDisplayName = showInformation && !isSentByMe,
                            
                            addTopMargin = if (!isIncoming) isFirstFromThisSender || isNextCus else false,
                            isIncoming = isIncoming,
                            cornersRadius = cornersRadius,
                            isPseudoBubble = messageContent.isPseudoBubble(),
                            timestampInsideMessage = messageContent.timestampInsideMessage(),
                            addMessageOverlay = messageContent.shouldAddMessageOverlay(),
                    )
                } else {
                    buildModernLayout(showInformation)
                }
            }
        }
        return messageLayout
    }

    private fun MessageContent?.isPseudoBubble(): Boolean {
        if (this == null) return false
        if (msgType == MessageType.MSGTYPE_LOCATION) return vectorPreferences.labsRenderLocationsInTimeline()
        return this.msgType in MSG_TYPES_WITH_PSEUDO_BUBBLE_LAYOUT
    }

    private fun MessageContent?.timestampInsideMessage(): Boolean {
        if (this == null) return false
        if (msgType == MessageType.MSGTYPE_LOCATION) return vectorPreferences.labsRenderLocationsInTimeline()
        return this.msgType in MSG_TYPES_WITH_TIMESTAMP_INSIDE_MESSAGE
    }

    private fun MessageContent?.shouldAddMessageOverlay(): Boolean {
        return when {
            this == null || msgType == MessageType.MSGTYPE_LIVE_LOCATION_STATE -> false
            msgType == MessageType.MSGTYPE_LOCATION                            -> vectorPreferences.labsRenderLocationsInTimeline()
            else                                                               -> msgType in MSG_TYPES_WITH_TIMESTAMP_INSIDE_MESSAGE
        }
    }

    private fun TimelineEvent.shouldBuildBubbleLayout(): Boolean {
        val type = root.getClearType()
        if (type in EVENT_TYPES_WITH_BUBBLE_LAYOUT) {
            val messageContent = getLastMessageContent()
            return messageContent?.msgType !in MSG_TYPES_WITHOUT_BUBBLE_LAYOUT
        }
        return false
    }

    private fun buildModernLayout(showInformation: Boolean): TimelineMessageLayout.Default {
        return TimelineMessageLayout.Default(
                showAvatar = showInformation,
                showDisplayName = showInformation,
                showTimestamp = showInformation || vectorPreferences.alwaysShowTimeStamps()
        )
    }

    private fun buildCornersRadius(izGroup: Boolean,
                                   isIncoming: Boolean,
                                   isFirstFromThisSender: Boolean,
                                   isLastFromThisSender: Boolean): TimelineMessageLayout.Bubble.CornersRadius {
        return if (izGroup) {
            if ((isIncoming && !isRTL) || (!isIncoming && isRTL)) {
                TimelineMessageLayout.Bubble.CornersRadius(
                        topStartRadius = cornerRadius,
                        topEndRadius = zGroupCornerRadius,
                        bottomStartRadius = cornerRadius,
                        bottomEndRadius = zGroupCornerRadius
                )
            } else {
                TimelineMessageLayout.Bubble.CornersRadius(
                        topStartRadius = zGroupCornerRadius,
                        topEndRadius = cornerRadius,
                        bottomStartRadius = zGroupCornerRadius,
                        bottomEndRadius = cornerRadius
                )
            }
        } else {
            if ((isIncoming && !isRTL) || (!isIncoming && isRTL)) {
                TimelineMessageLayout.Bubble.CornersRadius(
                        topStartRadius = cornerRadius,
                        topEndRadius = cornerRadius,
                        bottomStartRadius = cornerRadius,
                        bottomEndRadius = cornerRadius
                )
            } else {
                TimelineMessageLayout.Bubble.CornersRadius(
                        topStartRadius = cornerRadius,
                        topEndRadius = cornerRadius,
                        bottomStartRadius = cornerRadius,
                        bottomEndRadius = cornerRadius
                )
            }
        }
    }

    
    private fun isTileTypeMessage(event: TimelineEvent?): Boolean {
        return when (event?.root?.getClearType()) {
            EventType.KEY_VERIFICATION_DONE,
            EventType.KEY_VERIFICATION_CANCEL -> true
            EventType.MESSAGE_LOCAL,
            EventType.MESSAGE                 -> {
                event.getLastMessageContent() is MessageVerificationRequestContent
            }
            else                              -> false
        }
    }
}
