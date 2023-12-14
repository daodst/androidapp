

package im.vector.app.features.createdirect

import android.content.Context
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.BuildConfig
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.mvrx.runCatchingToAsync
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.utils.toast
import im.vector.app.features.analytics.AnalyticsTracker
import im.vector.app.features.analytics.plan.CreatedRoom
import im.vector.app.features.raw.wellknown.getElementWellknown
import im.vector.app.features.raw.wellknown.isE2EByDefault
import im.vector.app.features.userdirectory.PendingSelection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.raw.RawService
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.permalinks.PermalinkData
import org.matrix.android.sdk.api.session.permalinks.PermalinkParser
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.user.model.User

class CreateDirectRoomViewModel @AssistedInject constructor(@Assisted initialState: CreateDirectRoomViewState,
                                                            val context: Context,
                                                            val errorFormatter: ErrorFormatter,
                                                            val directRoomHelper: DirectRoomHelper, private val rawService: RawService, val session: Session, val analyticsTracker: AnalyticsTracker) : VectorViewModel<CreateDirectRoomViewState, CreateDirectRoomAction, CreateDirectRoomViewEvents>(
        initialState
) {

    @AssistedFactory interface Factory : MavericksAssistedViewModelFactory<CreateDirectRoomViewModel, CreateDirectRoomViewState> {
        override fun create(initialState: CreateDirectRoomViewState): CreateDirectRoomViewModel
    }

    companion object : MavericksViewModelFactory<CreateDirectRoomViewModel, CreateDirectRoomViewState> by hiltMavericksViewModelFactory()

    override fun handle(action: CreateDirectRoomAction) {
        when (action) {
            is CreateDirectRoomAction.CreateRoomAndInviteSelectedUsers -> onSubmitInvitees(action.selections)
            is CreateDirectRoomAction.QrScannedAction                  -> onCodeParsed(action)
        }
    }

    private fun onCodeParsed(action: CreateDirectRoomAction.QrScannedAction) {
        val mxid = (PermalinkParser.parse(action.result) as? PermalinkData.UserLink)?.userId

        if (mxid === null) {
            _viewEvents.post(CreateDirectRoomViewEvents.InvalidCode)
        } else {
            
            if (mxid.equals(other = session.myUserId, ignoreCase = true)) {
                _viewEvents.post(CreateDirectRoomViewEvents.DmSelf)
            } else {
                
                if (!BuildConfig.NEED_PAY) {
                    val qrInvitee = if (session.getUser(mxid) != null) session.getUser(mxid)!! else User(mxid, null, null)
                    onSubmitInvitees(setOf(PendingSelection.UserPendingSelection(qrInvitee)))
                } else {
                    _viewEvents.post(CreateDirectRoomViewEvents.QrId(mxid))
                }
            }
        }
    }

    
    private fun onSubmitInvitees(selections: Set<PendingSelection>) {
        val existingRoomId = selections.singleOrNull()?.getMxId()?.let { userId ->
            session.getExistingDirectRoomWithUser(userId)
        }
        if (existingRoomId != null) {
            
            setState {
                copy(createAndInviteState = Success(existingRoomId))
            }
        } else {
            
            createRoomAndInviteSelectedUsers(selections)
        }
    }

    private fun createRoomAndInviteSelectedUsers(selections: Set<PendingSelection>) {
        setState { copy(createAndInviteState = Loading()) }
        val isOnlyOne = selections.size == 1
        viewModelScope.launch(Dispatchers.IO) {
            if (isOnlyOne) {
                return@launch
            }
            selections.forEach {
                when (it) {
                    is PendingSelection.UserPendingSelection     -> {
                        
                        val roomId = session.getExistingDirectRoomWithUser(it.user.userId)
                        if (roomId.isNullOrEmpty() && it.user.shouldSendFlowers) {
                            try {
                                val newroomId = directRoomHelper.ensureDMExists(it.user.userId)
                                val room = session.getRoom(newroomId)
                                room?.sendGifts()
                            } catch (cause: Throwable) {
                                context.toast(errorFormatter.toHumanReadable(cause))
                            }
                        }
                    }
                    is PendingSelection.ThreePidPendingSelection -> Unit
                }
            }
        }


        viewModelScope.launch(Dispatchers.IO) {
            val adminE2EByDefault = rawService.getElementWellknown(session.sessionParams)?.isE2EByDefault() ?: true

            val roomParams = CreateRoomParams().apply {
                selections.forEach {
                    when (it) {
                        is PendingSelection.UserPendingSelection     -> invitedUserIds.add(it.user.userId)
                        is PendingSelection.ThreePidPendingSelection -> invite3pids.add(it.threePid)
                    }
                }
                setDirectMessage()
                enableEncryptionIfInvitedUsersSupportIt = adminE2EByDefault
            }

            val result = runCatchingToAsync {
                session.createRoom(roomParams)
            }
            if (isOnlyOne) {
                selections.elementAtOrNull(0)?.let {
                    if (it is PendingSelection.UserPendingSelection && it.user.shouldSendFlowers) {
                        result.invoke()?.let {
                            session.getRoom(it)?.sendGifts()
                        }
                    }
                }
            }
            analyticsTracker.capture(CreatedRoom(isDM = roomParams.isDirect.orFalse()))

            setState {
                copy(
                        createAndInviteState = result
                )
            }
        }
    }
}
