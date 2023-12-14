

package im.vector.app.features.home.room.detail.timeline.helper

import im.vector.app.R
import im.vector.app.core.date.DateFormatKind
import im.vector.app.core.date.VectorDateFormatter
import im.vector.app.core.extensions.localDateTime
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.home.room.detail.timeline.factory.TimelineItemFactoryParams
import im.vector.app.features.home.room.detail.timeline.item.E2EDecoration
import im.vector.app.features.home.room.detail.timeline.item.MessageInformationData
import im.vector.app.features.home.room.detail.timeline.item.PollResponseData
import im.vector.app.features.home.room.detail.timeline.item.PollVoteSummaryData
import im.vector.app.features.home.room.detail.timeline.item.ReferencesInfoData
import im.vector.app.features.home.room.detail.timeline.item.SendStateDecoration
import im.vector.app.features.home.room.detail.timeline.style.TimelineMessageLayoutFactory
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.izServerNoticeId
import org.matrix.android.sdk.api.session.crypto.verification.VerificationState
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.content.EncryptedEventContent
import org.matrix.android.sdk.api.session.events.model.isAttachmentMessage
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.ReferencesAggregatedContent
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.message.MessageVerificationRequestContent
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.room.timeline.hasBeenEdited
import javax.inject.Inject


class MessageInformationDataFactory @Inject constructor(private val session: Session,
                                                        private val dateFormatter: VectorDateFormatter,
                                                        private val stringProvider: StringProvider,
                                                        private val messageLayoutFactory: TimelineMessageLayoutFactory,
                                                        private val reactionsSummaryFactory: ReactionsSummaryFactory) {

    fun create(params: TimelineItemFactoryParams): MessageInformationData {
        val event = params.event
        val nextDisplayableEvent = params.nextDisplayableEvent
        val prevDisplayableEvent = params.prevDisplayableEvent
        val eventId = event.eventId
        val isSentByMe = event.root.senderId == session.myUserId
        val roomSummary = params.partialState.roomSummary

        val date = event.root.localDateTime()
        val nextDate = nextDisplayableEvent?.root?.localDateTime()
        val addDaySeparator = date.toLocalDate() != nextDate?.toLocalDate()

        val isFirstFromThisSender = nextDisplayableEvent?.root?.senderId != event.root.senderId || addDaySeparator
        val isLastFromThisSender = prevDisplayableEvent?.root?.senderId != event.root.senderId ||
                prevDisplayableEvent?.root?.localDateTime()?.toLocalDate() != date.toLocalDate()

        val time = dateFormatter.format(event.root.originServerTs, DateFormatKind.MESSAGE_SIMPLE)
        val e2eDecoration = getE2EDecoration(roomSummary, event)

        
        val sendStateDecoration = if (isSentByMe) {
            getSendStateDecoration(
                    event = event,
                    lastSentEventWithoutReadReceipts = params.lastSentEventIdWithoutReadReceipts,
                    isMedia = event.root.isAttachmentMessage()
            )
        } else {
            SendStateDecoration.NONE
        }

        val messageLayout = messageLayoutFactory.create(params)

        var displayName = event.senderInfo.disambiguatedDisplayName
        val izServerNotice = izServerNoticeId(event.roomId, session.myUserId)
        if (izServerNotice) {
            
            displayName = stringProvider.getString(R.string.server_notice_name)
        }
        return MessageInformationData(
                eventId = eventId,
                izServerNotice = izServerNotice,
                izOwner = params.izOwner,
                senderId = event.root.senderId ?: "",
                sendState = event.root.sendState,
                time = time,
                ageLocalTS = event.root.ageLocalTs,
                avatarUrl = event.senderInfo.avatarUrl,
                memberName = displayName,
                messageLayout = messageLayout,
                reactionsSummary = reactionsSummaryFactory.create(event),
                pollResponseAggregatedSummary = event.annotations?.pollResponseSummary?.let {
                    PollResponseData(
                            myVote = it.aggregatedContent?.myVote,
                            isClosed = it.closedTime != null,
                            votes = it.aggregatedContent?.votesSummary?.mapValues { votesSummary ->
                                PollVoteSummaryData(
                                        total = votesSummary.value.total,
                                        percentage = votesSummary.value.percentage
                                )
                            },
                            winnerVoteCount = it.aggregatedContent?.winnerVoteCount ?: 0,
                            totalVotes = it.aggregatedContent?.totalVotes ?: 0
                    )
                },
                hasBeenEdited = event.hasBeenEdited(),
                hasPendingEdits = event.annotations?.editSummary?.localEchos?.any() ?: false,
                referencesInfoData = event.annotations?.referencesAggregatedSummary?.let { referencesAggregatedSummary ->
                    val verificationState = referencesAggregatedSummary.content.toModel<ReferencesAggregatedContent>()?.verificationState
                            ?: VerificationState.REQUEST
                    ReferencesInfoData(verificationState)
                },
                sentByMe = isSentByMe,
                isFirstFromThisSender = isFirstFromThisSender,
                isLastFromThisSender = isLastFromThisSender,
                e2eDecoration = e2eDecoration,
                sendStateDecoration = sendStateDecoration
        )
    }

    private fun getSendStateDecoration(event: TimelineEvent,
                                       lastSentEventWithoutReadReceipts: String?,
                                       isMedia: Boolean): SendStateDecoration {
        val eventSendState = event.root.sendState
        return if (eventSendState.isSending()) {
            if (isMedia) SendStateDecoration.SENDING_MEDIA else SendStateDecoration.SENDING_NON_MEDIA
        } else if (eventSendState.hasFailed()) {
            SendStateDecoration.FAILED
        } else if (lastSentEventWithoutReadReceipts == event.eventId) {
            SendStateDecoration.SENT
        } else {
            SendStateDecoration.NONE
        }
    }

    private fun getE2EDecoration(roomSummary: RoomSummary?, event: TimelineEvent): E2EDecoration {
        return if (
                event.root.sendState == SendState.SYNCED &&
                roomSummary?.isEncrypted.orFalse() &&
                
                session.cryptoService().crossSigningService().getUserCrossSigningKeys(event.root.senderId ?: "")?.isTrusted() == true) {
            val ts = roomSummary?.encryptionEventTs ?: 0
            val eventTs = event.root.originServerTs ?: 0
            if (event.isEncrypted()) {
                
                if (event.root.getClearType() == EventType.ENCRYPTED || event.root.isRedacted()) {
                    E2EDecoration.NONE
                } else {
                    val sendingDevice = event.root.content
                            .toModel<EncryptedEventContent>()
                            ?.deviceId
                            ?.let { deviceId ->
                                session.cryptoService().getDeviceInfo(event.root.senderId ?: "", deviceId)
                            }
                    when {
                        sendingDevice == null                            -> {
                            
                            
                            E2EDecoration.NONE
                        }
                        sendingDevice.trustLevel == null                 -> {
                            E2EDecoration.WARN_SENT_BY_UNKNOWN
                        }
                        sendingDevice.trustLevel?.isVerified().orFalse() -> {
                            E2EDecoration.NONE
                        }
                        else                                             -> {
                            E2EDecoration.WARN_SENT_BY_UNVERIFIED
                        }
                    }
                }
            } else {
                if (event.root.isStateEvent()) {
                    
                    E2EDecoration.NONE
                } else {
                    
                    if (eventTs > ts) E2EDecoration.WARN_IN_CLEAR else E2EDecoration.NONE
                }
            }
        } else {
            E2EDecoration.NONE
        }
    }

    
    private fun isTileTypeMessage(event: TimelineEvent?): Boolean {
        return when (event?.root?.getClearType()) {
            EventType.KEY_VERIFICATION_DONE,
            EventType.KEY_VERIFICATION_CANCEL -> true
            EventType.MESSAGE                 -> {
                event.getLastMessageContent() is MessageVerificationRequestContent
            }
            else                              -> false
        }
    }
}
