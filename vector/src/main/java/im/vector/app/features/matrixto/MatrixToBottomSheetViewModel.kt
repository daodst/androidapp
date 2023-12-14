

package im.vector.app.features.matrixto

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.R
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.createdirect.DirectRoomHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.MatrixPatterns
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.permalinks.PermalinkData
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.peeking.PeekResult
import org.matrix.android.sdk.api.session.space.JoinSpaceResult
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.api.util.toMatrixItem

class MatrixToBottomSheetViewModel @AssistedInject constructor(
        @Assisted initialState: MatrixToBottomSheetState,
        private val session: Session,
        private val stringProvider: StringProvider,
        private val directRoomHelper: DirectRoomHelper,
        private val errorFormatter: ErrorFormatter
) : VectorViewModel<MatrixToBottomSheetState, MatrixToAction, MatrixToViewEvents>(initialState) {

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<MatrixToBottomSheetViewModel, MatrixToBottomSheetState> {
        override fun create(initialState: MatrixToBottomSheetState): MatrixToBottomSheetViewModel
    }

    companion object : MavericksViewModelFactory<MatrixToBottomSheetViewModel, MatrixToBottomSheetState> by hiltMavericksViewModelFactory()

    init {
        when (initialState.linkType) {
            is PermalinkData.RoomLink            -> {
                setState {
                    copy(roomPeekResult = Loading())
                }
            }
            is PermalinkData.UserLink            -> {
                setState {
                    copy(matrixItem = Loading())
                }
            }
            is PermalinkData.GroupLink           -> {
                
            }
            is PermalinkData.FallbackLink        -> {
                
            }
            is PermalinkData.RoomEmailInviteLink -> Unit
        }
        viewModelScope.launch(Dispatchers.IO) {
            resolveLink(initialState)
        }
    }

    private suspend fun resolveLink(initialState: MatrixToBottomSheetState) {
        val permalinkData = initialState.linkType
        if (permalinkData is PermalinkData.FallbackLink) {
            setState {
                copy(
                        matrixItem = Fail(IllegalArgumentException(stringProvider.getString(R.string.permalink_malformed))),
                        startChattingState = Uninitialized
                )
            }
            return
        }

        when (permalinkData) {
            is PermalinkData.UserLink     -> {
                val user = resolveUser(permalinkData.userId)
                setState {
                    copy(
                            matrixItem = Success(user.toMatrixItem()),
                            startChattingState = Success(Unit)
                    )
                }
            }
            is PermalinkData.RoomLink     -> {
                
                val knownRoom = if (permalinkData.isRoomAlias) {
                    tryOrNull {
                        session.getRoomIdByAlias(permalinkData.roomIdOrAlias, false)
                    }
                            ?.getOrNull()
                            ?.roomId?.let {
                                session.getRoom(it)
                            }
                } else {
                    session.getRoom(permalinkData.roomIdOrAlias)
                }
                        ?.roomSummary()
                        
                        ?.takeIf { it.membership == Membership.JOIN }
                if (knownRoom != null) {
                    setState {
                        copy(
                                roomPeekResult = Success(
                                        RoomInfoResult.FullInfo(
                                                roomItem = knownRoom.toMatrixItem(),
                                                name = knownRoom.name,
                                                topic = knownRoom.topic,
                                                memberCount = knownRoom.joinedMembersCount,
                                                alias = knownRoom.canonicalAlias,
                                                membership = knownRoom.membership,
                                                roomType = knownRoom.roomType,
                                                viaServers = null,
                                                isPublic = knownRoom.isPublic
                                        )
                                )
                        )
                    }
                } else {
                    val result = when (val peekResult = tryOrNull { resolveSpace(permalinkData) }) {
                        is PeekResult.Success           -> {
                            RoomInfoResult.FullInfo(
                                    roomItem = MatrixItem.RoomItem(peekResult.roomId, peekResult.name, peekResult.avatarUrl),
                                    name = peekResult.name ?: "",
                                    topic = peekResult.topic ?: "",
                                    memberCount = peekResult.numJoinedMembers,
                                    alias = peekResult.alias,
                                    membership = knownRoom?.membership ?: Membership.NONE,
                                    roomType = peekResult.roomType,
                                    viaServers = peekResult.viaServers.takeIf { it.isNotEmpty() } ?: permalinkData.viaParameters,
                                    isPublic = peekResult.isPublic
                            ).also {
                                peekResult.someMembers?.let { checkForKnownMembers(it) }
                            }
                        }
                        is PeekResult.PeekingNotAllowed -> {
                            RoomInfoResult.PartialInfo(
                                    roomId = permalinkData.roomIdOrAlias,
                                    viaServers = permalinkData.viaParameters
                            )
                        }
                        PeekResult.UnknownAlias         -> {
                            RoomInfoResult.UnknownAlias(permalinkData.roomIdOrAlias)
                        }
                        null                            -> {
                            RoomInfoResult.PartialInfo(
                                    roomId = permalinkData.roomIdOrAlias,
                                    viaServers = permalinkData.viaParameters
                            ).takeIf { permalinkData.isRoomAlias.not() }
                                    ?: RoomInfoResult.NotFound
                        }
                    }
                    setState {
                        copy(
                                roomPeekResult = Success(result)
                        )
                    }
                }
            }
            is PermalinkData.GroupLink    -> {
                
                _viewEvents.post(MatrixToViewEvents.Dismiss)
            }
            is PermalinkData.RoomEmailInviteLink,
            is PermalinkData.FallbackLink -> {
                _viewEvents.post(MatrixToViewEvents.Dismiss)
            }
        }
    }

    private fun checkForKnownMembers(someMembers: List<MatrixItem.UserItem>) {
        viewModelScope.launch(Dispatchers.Default) {
            val knownMembers = someMembers.filter {
                session.getExistingDirectRoomWithUser(it.id) != null
            }
            
            val finalRes = (knownMembers.filter { it.avatarUrl != null } + knownMembers.filter { it.avatarUrl == null })
                    .take(5)
            setState {
                copy(
                        peopleYouKnow = Success(finalRes)
                )
            }
        }
    }

    private suspend fun resolveSpace(permalinkData: PermalinkData.RoomLink): PeekResult {
        return resolveRoom(permalinkData.roomIdOrAlias)
    }

    private suspend fun resolveUser(userId: String): User {
        return tryOrNull { session.resolveUser(userId) }
        
                ?: User(userId, null, null)
    }

    
    private suspend fun resolveRoom(roomIdOrAlias: String): PeekResult {
        return session.peekRoom(roomIdOrAlias)
    }

    override fun handle(action: MatrixToAction) {
        when (action) {
            is MatrixToAction.StartChattingWithUser -> handleStartChatting(action)
            MatrixToAction.FailedToResolveUser      -> {
                _viewEvents.post(MatrixToViewEvents.Dismiss)
            }
            MatrixToAction.FailedToStartChatting    -> {
                _viewEvents.post(MatrixToViewEvents.Dismiss)
            }
            is MatrixToAction.JoinSpace             -> handleJoinSpace(action)
            is MatrixToAction.JoinRoom              -> handleJoinRoom(action)
            is MatrixToAction.OpenSpace             -> {
                _viewEvents.post(MatrixToViewEvents.NavigateToSpace(action.spaceID))
            }
            is MatrixToAction.OpenRoom              -> {
                _viewEvents.post(MatrixToViewEvents.NavigateToRoom(action.roomId))
            }
        }
    }

    private fun handleJoinSpace(joinSpace: MatrixToAction.JoinSpace) {
        setState {
            copy(startChattingState = Loading())
        }
        viewModelScope.launch {
            try {
                val joinResult = session.spaceService().joinSpace(joinSpace.spaceID, null, joinSpace.viaServers?.take(3) ?: emptyList())
                if (joinResult.isSuccess()) {
                    _viewEvents.post(MatrixToViewEvents.NavigateToSpace(joinSpace.spaceID))
                } else {
                    val errMsg = errorFormatter.toHumanReadable((joinResult as? JoinSpaceResult.Fail)?.error)
                    _viewEvents.post(MatrixToViewEvents.ShowModalError(errMsg))
                }
            } catch (failure: Throwable) {
                _viewEvents.post(MatrixToViewEvents.ShowModalError(errorFormatter.toHumanReadable(failure)))
            } finally {
                setState {
                    
                    copy(startChattingState = Uninitialized)
                }
            }
        }
    }

    private fun handleJoinRoom(action: MatrixToAction.JoinRoom) {
        setState {
            copy(startChattingState = Loading())
        }
        viewModelScope.launch {
            try {
                session.joinRoom(
                        roomIdOrAlias = action.roomIdOrAlias,
                        reason = null,
                        viaServers = action.viaServers?.take(3) ?: emptyList()
                )
                _viewEvents.post(MatrixToViewEvents.NavigateToRoom(getRoomIdFromRoomIdOrAlias(action.roomIdOrAlias)))
            } catch (failure: Throwable) {
                _viewEvents.post(MatrixToViewEvents.ShowModalError(errorFormatter.toHumanReadable(failure)))
            } finally {
                setState {
                    
                    copy(startChattingState = Uninitialized)
                }
            }
        }
    }

    private suspend fun getRoomIdFromRoomIdOrAlias(roomIdOrAlias: String): String {
        return if (MatrixPatterns.isRoomAlias(roomIdOrAlias)) {
            session.getRoomIdByAlias(roomIdOrAlias, true).get().roomId
        } else roomIdOrAlias
    }

    private fun handleStartChatting(action: MatrixToAction.StartChattingWithUser) {
        setState {
            copy(startChattingState = Loading())
        }
        viewModelScope.launch {
            val roomId = try {
                directRoomHelper.ensureDMExists(action.matrixItem.id)
            } catch (failure: Throwable) {
                setState {
                    copy(startChattingState = Fail(Exception(stringProvider.getString(R.string.invite_users_to_room_failure))))
                }
                return@launch
            }
            if (action.matrixItem is MatrixItem.UserItem && action.matrixItem.shouldSendFlowers) {
                session.getRoom(roomId)?.sendGifts()
            }
            setState {
                
                copy(startChattingState = Uninitialized)
            }
            _viewEvents.post(MatrixToViewEvents.NavigateToRoom(roomId))
        }
    }
}
