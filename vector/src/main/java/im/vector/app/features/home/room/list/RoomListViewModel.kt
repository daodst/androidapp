

package im.vector.app.features.home.room.list

import androidx.lifecycle.MutableLiveData
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.google.gson.Gson
import common.app.pojo.ChatWidgetItemEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.AppStateHandler
import im.vector.app.RoomGroupingMethod
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.displayname.getBestName
import im.vector.app.features.invite.AutoAcceptInvites
import im.vector.app.features.settings.VectorPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.izServerNoticeId
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.session.room.UpdatableLivePageResult
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.tag.RoomTag
import org.matrix.android.sdk.api.session.room.state.isPublic
import org.matrix.android.sdk.api.util.toMatrixItem
import org.matrix.android.sdk.flow.flow
import timber.log.Timber
import kotlin.math.abs

class RoomListViewModel @AssistedInject constructor(
        @Assisted initialState: RoomListViewState,
        private val activeSessionHolder: ActiveSessionHolder,
        private val session: Session,
        stringProvider: StringProvider,
        appStateHandler: AppStateHandler,
        vectorPreferences: VectorPreferences,
        autoAcceptInvites: AutoAcceptInvites,
) : VectorViewModel<RoomListViewState, RoomListAction, RoomListViewEvents>(initialState) {

    @AssistedFactory interface Factory : MavericksAssistedViewModelFactory<RoomListViewModel, RoomListViewState> {
        override fun create(initialState: RoomListViewState): RoomListViewModel
    }

    private var updatableQuery: UpdatableLivePageResult? = null

    private val suggestedRoomJoiningState: MutableLiveData<Map<String, Async<Unit>>> = MutableLiveData(emptyMap())

    
    private val mTypeMessage = hashMapOf<Int, List<ChatWidgetItemEntity>>()

    interface ActiveSpaceQueryUpdater {
        fun updateForSpaceId(roomId: String?)
    }

    enum class SpaceFilterStrategy {
        
        ORPHANS_IF_SPACE_NULL,

        
        ALL_IF_SPACE_NULL,

        
        NONE
    }

    init {
        observeMembershipChanges()

        appStateHandler.selectedRoomGroupingFlow.distinctUntilChanged().execute {
            copy(currentRoomGrouping = it.invoke()?.orNull()?.let { Success(it) } ?: Loading())
        }

        session.flow().liveUser(session.myUserId).map { it.getOrNull()?.toMatrixItem()?.getBestName() }.distinctUntilChanged().execute {
            copy(
                    currentUserName = it.invoke() ?: session.myUserId
            )
        }
    }

    private fun observeMembershipChanges() {
        session.flow().liveRoomChangeMembershipState().setOnEach {
            copy(roomMembershipChanges = it)
        }
    }

    companion object : MavericksViewModelFactory<RoomListViewModel, RoomListViewState> by hiltMavericksViewModelFactory()

    private val roomListSectionBuilder = if (appStateHandler.getCurrentRoomGroupingMethod() is RoomGroupingMethod.BySpace) {
        RoomListSectionBuilderSpace(
                session, stringProvider, appStateHandler, viewModelScope, autoAcceptInvites, {
            updatableQuery = it
        }, suggestedRoomJoiningState, !vectorPreferences.prefSpacesShowAllRoomInHome()
        )
    } else {
        RoomListSectionBuilderGroup(
                viewModelScope, session, stringProvider, appStateHandler, autoAcceptInvites
        ) {
            updatableQuery = it
        }
    }

    val sections: List<RoomsSection> by lazy {
        roomListSectionBuilder.buildSections(initialState.displayMode)
    }

    override fun handle(action: RoomListAction) {
        when (action) {
            is RoomListAction.RoomGroup                   -> handleRoomGroup(action)
            is RoomListAction.SelectRoom                  -> handleSelectRoom(action)
            is RoomListAction.AcceptInvitation            -> handleAcceptInvitation(action)
            is RoomListAction.RejectInvitation            -> handleRejectInvitation(action)
            is RoomListAction.FilterWith                  -> handleFilter(action)
            is RoomListAction.LeaveRoom                   -> handleLeaveRoom(action)
            is RoomListAction.ChangeRoomNotificationState -> handleChangeNotificationMode(action)
            is RoomListAction.ToggleTag                   -> handleToggleTag(action)
            is RoomListAction.ToggleSection               -> handleToggleSection(action.section)
            is RoomListAction.JoinSuggestedRoom           -> handleJoinSuggestedRoom(action)
            is RoomListAction.ShowRoomDetails             -> handleShowRoomDetails(action)
            is RoomListAction.SendWidgetBroadCast         -> handleSendWidgetBroadCast(action)
        }
    }

    private fun handleRoomGroup(action: RoomListAction.RoomGroup) {
        session.getRoom(action.roomId)?.setRoomSummaryGroupStatus(action.isGroup)
    }

    fun isPublicRoom(roomId: String): Boolean {
        return session.getRoom(roomId)?.isPublic().orFalse()
    }

    

    private fun handleSelectRoom(action: RoomListAction.SelectRoom) = withState {
        _viewEvents.post(RoomListViewEvents.SelectRoom(action.roomSummary, false))
    }

    private fun handleToggleSection(roomSection: RoomsSection) {
        roomSection.isExpanded.postValue(!roomSection.isExpanded.value.orFalse())
        
    }

    private fun handleFilter(action: RoomListAction.FilterWith) {
        setState {
            copy(
                    roomFilter = action.filter
            )
        }
        updatableQuery?.apply {
            queryParams = queryParams.copy(
                    displayName = QueryStringValue.Contains(action.filter, QueryStringValue.Case.NORMALIZED)
            )
        }
    }

    private fun handleAcceptInvitation(action: RoomListAction.AcceptInvitation) = withState { state ->
        val roomId = action.roomSummary.roomId
        val roomMembershipChange = state.roomMembershipChanges[roomId]
        if (roomMembershipChange?.isInProgress().orFalse()) {
            
            Timber.w("Try to join an already joining room. Should not happen")
            return@withState
        }
        _viewEvents.post(RoomListViewEvents.SelectRoom(action.roomSummary, true))

        
        setState {
            copy(roomMembershipChanges = roomMembershipChanges.mapValues {
                if (it.key == roomId) {
                    ChangeMembershipState.Joining
                } else {
                    it.value
                }
            })
        }
    }

    private fun handleRejectInvitation(action: RoomListAction.RejectInvitation) = withState { state ->
        val roomId = action.roomSummary.roomId
        val roomMembershipChange = state.roomMembershipChanges[roomId]
        if (roomMembershipChange?.isInProgress().orFalse()) {
            
            Timber.w("Try to left an already leaving or joining room. Should not happen")
            return@withState
        }

        viewModelScope.launch {
            try {
                session.leaveRoom(roomId)
                
                
                
                
            } catch (failure: Throwable) {
                
                _viewEvents.post(RoomListViewEvents.Failure(failure))
            }
        }
    }

    private fun handleChangeNotificationMode(action: RoomListAction.ChangeRoomNotificationState) {
        val room = session.getRoom(action.roomId)
        if (room != null) {
            viewModelScope.launch {
                try {
                    room.setRoomNotificationState(action.notificationState)
                } catch (failure: Exception) {
                    _viewEvents.post(RoomListViewEvents.Failure(failure))
                }
            }
        }
    }

    private fun handleJoinSuggestedRoom(action: RoomListAction.JoinSuggestedRoom) {
        suggestedRoomJoiningState.postValue(suggestedRoomJoiningState.value.orEmpty().toMutableMap().apply {
            this[action.roomId] = Loading()
        }.toMap())

        viewModelScope.launch {
            try {
                session.joinRoom(action.roomId, null, action.viaServers ?: emptyList())

                suggestedRoomJoiningState.postValue(suggestedRoomJoiningState.value.orEmpty().toMutableMap().apply {
                    this[action.roomId] = Success(Unit)
                }.toMap())
            } catch (failure: Throwable) {
                suggestedRoomJoiningState.postValue(suggestedRoomJoiningState.value.orEmpty().toMutableMap().apply {
                    this[action.roomId] = Fail(failure)
                }.toMap())
            }
        }
    }

    private fun handleShowRoomDetails(action: RoomListAction.ShowRoomDetails) {
        session.permalinkService().createRoomPermalink(action.roomId, action.viaServers)?.let {
            _viewEvents.post(RoomListViewEvents.NavigateToMxToBottomSheet(it))
        }
    }

    private fun handleToggleTag(action: RoomListAction.ToggleTag) {
        session.getRoom(action.roomId)?.let { room ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    if (room.roomSummary()?.hasTag(action.tag) == false) {
                        
                        action.tag.otherTag()?.takeIf { room.roomSummary()?.hasTag(it).orFalse() }?.let { tagToRemove ->
                            room.deleteTag(tagToRemove)
                        }

                        
                        room.addTag(action.tag, 0.5)
                    } else {
                        room.deleteTag(action.tag)
                    }
                } catch (failure: Throwable) {
                    _viewEvents.post(RoomListViewEvents.Failure(failure))
                }
            }
        }
    }

    private fun String.otherTag(): String? {
        return when (this) {
            RoomTag.ROOM_TAG_FAVOURITE    -> RoomTag.ROOM_TAG_LOW_PRIORITY
            RoomTag.ROOM_TAG_LOW_PRIORITY -> RoomTag.ROOM_TAG_FAVOURITE
            else                          -> null
        }
    }

    private fun handleLeaveRoom(action: RoomListAction.LeaveRoom) {
        _viewEvents.post(RoomListViewEvents.Loading(null))
        viewModelScope.launch {
            val value = runCatching { session.leaveRoom(action.roomId) }.fold({ RoomListViewEvents.Done }, { RoomListViewEvents.Failure(it) })
            _viewEvents.post(value)
        }
    }

    private fun handleSendWidgetBroadCast(action: RoomListAction.SendWidgetBroadCast) {
        
        mTypeMessage.put(action.index, getTypeData(action.data))

        val mWidgetItems = arrayListOf<ChatWidgetItemEntity>()
        mTypeMessage.map { (key, value) ->
            mWidgetItems.addAll(value)
        }
        
        if (mWidgetItems.size > 5) mWidgetItems.subList(0, 5)
        
        _viewEvents.post(RoomListViewEvents.SendWidgetBroadCast(Gson().toJson(mWidgetItems)))
    }

    private fun getTypeData(data: List<RoomSummary>): List<ChatWidgetItemEntity> {
        val mWidgetItems = arrayListOf<ChatWidgetItemEntity>()
        for (roomSummary in data) {
            
            if (roomSummary.hasUnreadMessages || roomSummary.atMsg.isNotEmpty()) {
                val matrixItem = roomSummary.toMatrixItem()
                var hash = 0
                matrixItem.id.toList().map { chr -> hash = (hash shl 5) - hash + chr.code }

                val avatarUrl = activeSessionHolder.getSafeActiveSession()?.contentUrlResolver()?.resolveThumbnail(
                        roomSummary.avatarUrl,
                        250,
                        250,
                        ContentUrlResolver.ThumbnailMethod.SCALE
                )
                
                
                val izServerNotice = izServerNoticeId(roomSummary.roomId, session.myUserId)
                val entity = ChatWidgetItemEntity(
                        abs(hash),
                        roomSummary.displayName,
                        roomSummary.roomId,
                        roomSummary.notificationCount + roomSummary.atMsg.size,
                        avatarUrl,
                        izServerNotice
                )
                mWidgetItems.add(entity)
            }
        }
        return mWidgetItems
    }
}
