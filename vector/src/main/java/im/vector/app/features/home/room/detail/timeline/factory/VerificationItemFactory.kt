
package im.vector.app.features.home.room.detail.timeline.factory

import im.vector.app.R
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.resources.UserPreferencesProvider
import im.vector.app.features.home.room.detail.timeline.MessageColorProvider
import im.vector.app.features.home.room.detail.timeline.helper.AvatarSizeProvider
import im.vector.app.features.home.room.detail.timeline.helper.MessageInformationDataFactory
import im.vector.app.features.home.room.detail.timeline.helper.MessageItemAttributesFactory
import im.vector.app.features.home.room.detail.timeline.item.StatusTileTimelineItem
import im.vector.app.features.home.room.detail.timeline.item.StatusTileTimelineItem_
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.verification.CancelCode
import org.matrix.android.sdk.api.session.crypto.verification.VerificationState
import org.matrix.android.sdk.api.session.crypto.verification.safeValueOf
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageRelationContent
import org.matrix.android.sdk.api.session.room.model.message.MessageVerificationCancelContent
import javax.inject.Inject


class VerificationItemFactory @Inject constructor(
        private val messageColorProvider: MessageColorProvider,
        private val messageInformationDataFactory: MessageInformationDataFactory,
        private val messageItemAttributesFactory: MessageItemAttributesFactory,
        private val avatarSizeProvider: AvatarSizeProvider,
        private val noticeItemFactory: NoticeItemFactory,
        private val userPreferencesProvider: UserPreferencesProvider,
        private val stringProvider: StringProvider,
        private val session: Session
) {

    fun create(params: TimelineItemFactoryParams): VectorEpoxyModel<*>? {
        val event = params.event
        if (event.root.eventId == null) return null

        val relContent: MessageRelationContent = event.root.content.toModel()
                ?: event.root.getClearContent().toModel()
                ?: return ignoredConclusion(params)

        if (relContent.relatesTo?.type != RelationType.REFERENCE) return ignoredConclusion(params)
        val refEventId = relContent.relatesTo?.eventId
                ?: return ignoredConclusion(params)

        
        val refEvent = session.getRoom(event.root.roomId ?: "")?.getTimelineEvent(refEventId)
                ?: return ignoredConclusion(params)

        
        

        val referenceInformationData = messageInformationDataFactory.create(TimelineItemFactoryParams(event = refEvent))

        val informationData = messageInformationDataFactory.create(params)
        val attributes = messageItemAttributesFactory.create(null, informationData, params.callback, params.reactionsSummaryEvents)

        when (event.root.getClearType()) {
            EventType.KEY_VERIFICATION_CANCEL -> {
                
                val cancelContent = event.root.getClearContent().toModel<MessageVerificationCancelContent>()
                        ?: return ignoredConclusion(params)

                when (safeValueOf(cancelContent.code)) {
                    CancelCode.MismatchedCommitment,
                    CancelCode.MismatchedKeys,
                    CancelCode.MismatchedSas -> {
                        
                        return StatusTileTimelineItem_()
                                .attributes(
                                        StatusTileTimelineItem.Attributes(
                                                title = stringProvider.getString(R.string.verification_conclusion_warning),
                                                description = "${informationData.memberName} (${informationData.senderId})",
                                                shieldUIState = StatusTileTimelineItem.ShieldUIState.RED,
                                                informationData = informationData,
                                                avatarRenderer = attributes.avatarRenderer,
                                                messageColorProvider = messageColorProvider,
                                                emojiTypeFace = attributes.emojiTypeFace,
                                                itemClickListener = attributes.itemClickListener,
                                                itemLongClickListener = attributes.itemLongClickListener,
                                                reactionPillCallback = attributes.reactionPillCallback,
                                                readReceiptsCallback = attributes.readReceiptsCallback,
                                                reactionsSummaryEvents = attributes.reactionsSummaryEvents
                                        )
                                )
                                .highlighted(params.isHighlighted)
                                .leftGuideline(avatarSizeProvider.leftGuideline)
                    }
                    else                     -> return ignoredConclusion(params)
                }
            }
            EventType.KEY_VERIFICATION_DONE   -> {
                
                if (referenceInformationData.referencesInfoData?.verificationStatus != VerificationState.DONE) {
                    return ignoredConclusion(params)
                }
                

                if (informationData.sentByMe) {
                    
                    return ignoredConclusion(params)
                }
                return StatusTileTimelineItem_()
                        .attributes(
                                StatusTileTimelineItem.Attributes(
                                        title = stringProvider.getString(R.string.sas_verified),
                                        description = "${informationData.memberName} (${informationData.senderId})",
                                        shieldUIState = StatusTileTimelineItem.ShieldUIState.GREEN,
                                        informationData = informationData,
                                        avatarRenderer = attributes.avatarRenderer,
                                        messageColorProvider = messageColorProvider,
                                        emojiTypeFace = attributes.emojiTypeFace,
                                        itemClickListener = attributes.itemClickListener,
                                        itemLongClickListener = attributes.itemLongClickListener,
                                        reactionPillCallback = attributes.reactionPillCallback,
                                        readReceiptsCallback = attributes.readReceiptsCallback,
                                        reactionsSummaryEvents = attributes.reactionsSummaryEvents
                                )
                        )
                        .highlighted(params.isHighlighted)
                        .leftGuideline(avatarSizeProvider.leftGuideline)
            }
        }
        return null
    }

    private fun ignoredConclusion(params: TimelineItemFactoryParams): VectorEpoxyModel<*>? {
        if (userPreferencesProvider.shouldShowHiddenEvents()) return noticeItemFactory.create(params)
        return null
    }
}
