

package im.vector.app.features.home.room.list

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import im.vector.app.R
import im.vector.app.core.date.DateFormatKind
import im.vector.app.core.date.VectorDateFormatter
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.home.room.detail.timeline.format.DisplayableEventFormatter
import im.vector.app.features.home.room.typing.TypingHelper
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.getServerNoticeId
import org.matrix.android.sdk.api.session.call.izServerNoticeId
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.room.timeline.isLocal
import org.matrix.android.sdk.api.util.toMatrixItem
import timber.log.Timber
import javax.inject.Inject

class RoomSummaryItemFactory @Inject constructor(private val session: Session, private val displayableEventFormatter: DisplayableEventFormatter, private val dateFormatter: VectorDateFormatter, private val stringProvider: StringProvider, private val dimensionConverter: DimensionConverter, private val typingHelper: TypingHelper, private val avatarRenderer: AvatarRenderer, private val errorFormatter: ErrorFormatter) {

    fun create(roomSummary: RoomSummary, roomChangeMembershipStates: Map<String, ChangeMembershipState>, selectedRoomIds: Set<String>, listener: RoomListListener?): VectorEpoxyModel<*> {
        return when (roomSummary.membership) {
            Membership.INVITE -> {
                val changeMembershipState = roomChangeMembershipStates[roomSummary.roomId] ?: ChangeMembershipState.Unknown
                createInvitationItem(roomSummary, changeMembershipState, listener)
            }
            else              -> createRoomItem(roomSummary, selectedRoomIds, listener?.let { it::onRoomClicked }, listener?.let { it::onRoomLongClicked })
        }
    }

    fun createSuggestion(spaceChildInfo: SpaceChildInfo, suggestedRoomJoiningStates: Map<String, Async<Unit>>, listener: RoomListListener?): VectorEpoxyModel<*> {
        val error = (suggestedRoomJoiningStates[spaceChildInfo.childRoomId] as? Fail)?.error
        return SpaceChildInfoItem_().id("sug_${spaceChildInfo.childRoomId}").matrixItem(spaceChildInfo.toMatrixItem()).avatarRenderer(avatarRenderer).topic(
                spaceChildInfo.topic
        ).errorLabel(error?.let {
            stringProvider.getString(R.string.error_failed_to_join_room, errorFormatter.toHumanReadable(it))
        }).buttonLabel(
                if (error != null) stringProvider.getString(R.string.global_retry)
                else stringProvider.getString(R.string.action_join)
        ).loading(suggestedRoomJoiningStates[spaceChildInfo.childRoomId] is Loading).memberCount(
                spaceChildInfo.activeMemberCount ?: 0
        ).buttonClickListener { listener?.onJoinSuggestedRoom(spaceChildInfo) }.itemClickListener { listener?.onSuggestedRoomClicked(spaceChildInfo) }
    }

    private fun createInvitationItem(roomSummary: RoomSummary, changeMembershipState: ChangeMembershipState, listener: RoomListListener?): VectorEpoxyModel<*> {
        val secondLine = if (roomSummary.isDirect) {
            roomSummary.inviterId
        } else {
            roomSummary.inviterId?.let {
                stringProvider.getString(R.string.invited_by, it)
            }
        }

        return RoomInvitationItem_().id(roomSummary.roomId).avatarRenderer(avatarRenderer).matrixItem(roomSummary.toMatrixItem()).secondLine(secondLine).changeMembershipState(
                changeMembershipState
        ).acceptListener { listener?.onAcceptRoomInvitation(roomSummary) }.rejectListener { listener?.onRejectRoomInvitation(roomSummary) }.listener {
            listener?.onRoomClicked(
                    roomSummary
            )
        }
    }

    fun createRoomItem(roomSummary: RoomSummary, selectedRoomIds: Set<String>, onClick: ((RoomSummary) -> Unit)?, onLongClick: ((RoomSummary) -> Boolean)?): VectorEpoxyModel<*> {

        val unreadCount = roomSummary.notificationCount
        val showHighlighted = roomSummary.highlightCount > 0
        val showSelected = selectedRoomIds.contains(roomSummary.roomId)
        var latestFormattedEvent: CharSequence = ""
        var latestEventTime = ""
        val latestEvent = roomSummary.latestPreviewableEvent
        if (latestEvent != null) {
            latestFormattedEvent =
                    displayableEventFormatter.format(latestEvent, roomSummary.isDirect, roomSummary.isDirect.not(), atMsgEmpty = roomSummary.atMsg.isEmpty())
            latestEventTime = dateFormatter.format(latestEvent.root.originServerTs, DateFormatKind.ROOM_LIST)
        }
        val typingMessage = typingHelper.getTypingMessage(roomSummary.typingUsers)

        Timber.i("----RoomSummaryItemFactory---------$latestEvent-------${latestFormattedEvent}-----------------------------------")
        val isOwner = roomSummary.owner == session.myOriginUId

        val izNotice = latestEvent?.let {
            it.getLastMessageContent().isLocal()
        } == true
        val izServerNotice = izServerNoticeId(roomSummary.roomId, session.myUserId)

        Timber.i("-------RoomSummaryItemFactory----$izServerNotice-------${getServerNoticeId(session.myUserId)}--------$roomSummary------------")

        return RoomSummaryItem_().id(roomSummary.roomId).avatarRenderer(avatarRenderer)
                
                .dstWidth(dimensionConverter.dpToPx(22))
                
                
                .izOwner(isOwner)
                .izServerNoticeGroup(izServerNotice)
                .izNotice(izNotice)
                
                .izDvmGroup(roomSummary.isDvmGroup)
                
                .hasAtInfo(roomSummary.atMsg.isNotEmpty())
                
                .izGroup(roomSummary.isGroup)
                
                .hasRedEnvelope(true)
                
                .lastFormattedEvent(latestFormattedEvent.toEpoxyCharSequence())
                
                
                .izPublic(roomSummary.isPublic).showPresence(roomSummary.isDirect).userPresence(roomSummary.directUserPresence).matrixItem(roomSummary.toMatrixItem()).lastEventTime(
                        latestEventTime
                ).typingMessage(typingMessage).showHighlighted(showHighlighted).showSelected(
                        showSelected
                ).hasFailedSending(roomSummary.hasFailedSending).unreadNotificationCount(unreadCount + roomSummary.atMsg.size).hasUnreadMessage(roomSummary.hasUnreadMessages || roomSummary.atMsg.isNotEmpty()).hasDraft(
                        roomSummary.userDrafts.isNotEmpty()
                ).itemLongClickListener { _ ->
                    onLongClick?.invoke(roomSummary) ?: false
                }.itemClickListener { onClick?.invoke(roomSummary) }
    }
}
