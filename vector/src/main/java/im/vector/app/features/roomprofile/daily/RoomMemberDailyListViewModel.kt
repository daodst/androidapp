

package im.vector.app.features.roomprofile.daily

import androidx.lifecycle.asFlow
import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.platform.EmptyViewEvents
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.features.powerlevel.PowerLevelsFlowFactory
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.model.RoomEncryptionTrustLevel
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import org.matrix.android.sdk.flow.flow
import org.matrix.android.sdk.flow.mapOptional
import org.matrix.android.sdk.flow.unwrap
import timber.log.Timber

class RoomMemberDailyListViewModel @AssistedInject constructor(@Assisted initialState: RoomMemberListViewState,
                                                               private val roomMemberSummaryComparator: RoomMemberDailySummaryComparator,
                                                               private val session: Session) :
        VectorViewModel<RoomMemberListViewState, RoomMemberDailyListAction, EmptyViewEvents>(initialState) {

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<RoomMemberDailyListViewModel, RoomMemberListViewState> {
        override fun create(initialState: RoomMemberListViewState): RoomMemberDailyListViewModel
    }

    companion object : MavericksViewModelFactory<RoomMemberDailyListViewModel, RoomMemberListViewState> by hiltMavericksViewModelFactory()

    private val room = session.getRoom(initialState.roomId)!!

    init {
        observeRoomMemberSummaries()
        observeThirdPartyInvites()
        observeRoomSummary()
        observePowerLevel()
        observeIgnoredUsers()
    }

    private fun observeRoomMemberSummaries() {
        val roomMemberQueryParams = roomMemberQueryParams {
            displayName = QueryStringValue.IsNotEmpty
            memberships = Membership.activeMemberships()
        }

        combine(
                room.flow().liveRoomMembers(roomMemberQueryParams),
                room.flow()
                        .liveStateEvent(EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.NoCondition)
                        .mapOptional { it.content.toModel<PowerLevelsContent>() }
                        .unwrap()
        ) { roomMembers, powerLevelsContent ->
            buildRoomMemberSummaries(powerLevelsContent, roomMembers)
        }
                .execute { async ->
                    copy(roomMemberSummaries = async)
                }

        if (room.isEncrypted()) {
            room.flow().liveRoomMembers(roomMemberQueryParams)
                    .flatMapLatest { membersSummary ->
                        session.cryptoService().getLiveCryptoDeviceInfo(membersSummary.map { it.userId })
                                .asFlow()
                                .catch { Timber.e(it) }
                                .map { deviceList ->
                                    
                                    deviceList.groupBy { it.userId }.mapValues {
                                        val allDeviceTrusted = it.value.fold(it.value.isNotEmpty()) { prev, next ->
                                            prev && next.trustLevel?.isCrossSigningVerified().orFalse()
                                        }
                                        if (session.cryptoService().crossSigningService().getUserCrossSigningKeys(it.key)?.isTrusted().orFalse()) {
                                            if (allDeviceTrusted) RoomEncryptionTrustLevel.Trusted else RoomEncryptionTrustLevel.Warning
                                        } else {
                                            RoomEncryptionTrustLevel.Default
                                        }
                                    }
                                }
                    }
                    .execute { async ->
                        copy(trustLevelMap = async)
                    }
        }
    }

    private fun observePowerLevel() {
        PowerLevelsFlowFactory(room).createFlow()
                .onEach {
                    val permissions = ActionPermissions(
                            canInvite = PowerLevelsHelper(it).isUserAbleToInvite(session.myUserId),
                            canRevokeThreePidInvite = PowerLevelsHelper(it).isUserAllowedToSend(
                                    userId = session.myUserId,
                                    isState = true,
                                    eventType = EventType.STATE_ROOM_THIRD_PARTY_INVITE
                            )
                    )
                    setState {
                        copy(actionsPermissions = permissions)
                    }
                }.launchIn(viewModelScope)
    }

    private fun observeRoomSummary() {
        room.flow().liveRoomSummary()
                .unwrap()
                .execute { async ->
                    copy(roomSummary = async)
                }
    }

    private fun observeThirdPartyInvites() {
        room.flow().liveStateEvents(setOf(EventType.STATE_ROOM_THIRD_PARTY_INVITE))
                .execute { async ->
                    copy(threePidInvites = async)
                }
    }

    private fun observeIgnoredUsers() {
        session.flow()
                .liveIgnoredUsers()
                .execute { async ->
                    copy(
                            ignoredUserIds = async.invoke().orEmpty().map { it.userId }
                    )
                }
    }

    private fun buildRoomMemberSummaries(powerLevelsContent: PowerLevelsContent, roomMembers: List<RoomMemberSummary>): RoomMemberSummaries {
        val admins = ArrayList<RoomMemberSummary>()
        val moderators = ArrayList<RoomMemberSummary>()
        val users = ArrayList<RoomMemberSummary>(roomMembers.size)
        val customs = ArrayList<RoomMemberSummary>()
        val invites = ArrayList<RoomMemberSummary>()
        val powerLevelsHelper = PowerLevelsHelper(powerLevelsContent)
        roomMembers
                .forEach { roomMember ->
                    val userRole = powerLevelsHelper.getUserRole(roomMember.userId)
                    when {
                        roomMember.membership == Membership.INVITE -> invites.add(roomMember)
                        userRole == Role.Admin                     -> admins.add(roomMember)
                        userRole == Role.Moderator                 -> moderators.add(roomMember)
                        userRole == Role.Default                   -> users.add(roomMember)
                        else                                       -> customs.add(roomMember)
                    }
                }

        return listOf(
                RoomMemberListCategories.ADMIN to admins.sortedWith(roomMemberSummaryComparator),
                RoomMemberListCategories.MODERATOR to moderators.sortedWith(roomMemberSummaryComparator),
                RoomMemberListCategories.CUSTOM to customs.sortedWith(roomMemberSummaryComparator),
                RoomMemberListCategories.INVITE to invites.sortedWith(roomMemberSummaryComparator),
                RoomMemberListCategories.USER to users.sortedWith(roomMemberSummaryComparator)
        )
    }

    override fun handle(action: RoomMemberDailyListAction) {
        when (action) {
            is RoomMemberDailyListAction.RevokeThreePidInvite -> handleRevokeThreePidInvite(action)
            is RoomMemberDailyListAction.FilterMemberList     -> handleFilterMemberList(action)
        }
    }

    private fun handleRevokeThreePidInvite(action: RoomMemberDailyListAction.RevokeThreePidInvite) {
        viewModelScope.launch {
            room.sendStateEvent(
                    eventType = EventType.STATE_ROOM_THIRD_PARTY_INVITE,
                    stateKey = action.stateKey,
                    body = emptyMap()
            )
        }
    }

    private fun handleFilterMemberList(action: RoomMemberDailyListAction.FilterMemberList) {
        setState {
            copy(
                    filter = action.searchTerm
            )
        }
    }
}
