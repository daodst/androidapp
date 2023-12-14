

package im.vector.app.features.invite

import android.content.Context
import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.R
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.utils.toast
import im.vector.app.features.createdirect.DirectRoomHelper
import im.vector.app.features.userdirectory.PendingSelection
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.flow.flow
import org.matrix.android.sdk.flow.unwrap

class InviteUsersToRoomViewModel @AssistedInject constructor(
        @Assisted initialState: InviteUsersToRoomViewState,
        val session: Session,
        val directRoomHelper: DirectRoomHelper,
        val context: Context,
        val errorFormatter: ErrorFormatter,
        val stringProvider: StringProvider
) : VectorViewModel<InviteUsersToRoomViewState, InviteUsersToRoomAction, InviteUsersToRoomViewEvents>(initialState) {

    private val room = session.getRoom(initialState.roomId)!!

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<InviteUsersToRoomViewModel, InviteUsersToRoomViewState> {
        override fun create(initialState: InviteUsersToRoomViewState): InviteUsersToRoomViewModel
    }

    companion object : MavericksViewModelFactory<InviteUsersToRoomViewModel, InviteUsersToRoomViewState> by hiltMavericksViewModelFactory()

    override fun handle(action: InviteUsersToRoomAction) {
        when (action) {

            is InviteUsersToRoomAction.InviteSelectedUsers -> inviteUsersToRoom(action.selections)
        }
    }

    init {
        observeRoomSummary()
        room.getRoomSummaryLive()
    }

    private fun observeRoomSummary() {
        room.flow().liveRoomSummary().unwrap().execute { async ->
            copy(
                    asyncRoomSummary = async
            )
        }
    }

    private fun inviteUsersToRoom(selections: Set<PendingSelection>) {

        viewModelScope.launch {

            _viewEvents.post(InviteUsersToRoomViewEvents.Loading)

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


            selections.asFlow()
                    .map { user ->
                        when (user) {
                            is PendingSelection.UserPendingSelection     -> room.invite(user.user.userId, null)
                            is PendingSelection.ThreePidPendingSelection -> room.invite3pid(user.threePid)
                        }
                    }
                    .catch { cause ->
                        _viewEvents.post(InviteUsersToRoomViewEvents.Failure(cause))
                    }
                    .collect {
                        val successMessage = when (selections.size) {
                            1    -> stringProvider.getString(
                                    R.string.invitation_sent_to_one_user,
                                    selections.first().getBestName()
                            )
                            2    -> stringProvider.getString(
                                    R.string.invitations_sent_to_two_users,
                                    selections.first().getBestName(),
                                    selections.last().getBestName()
                            )
                            else -> stringProvider.getQuantityString(
                                    R.plurals.invitations_sent_to_one_and_more_users,
                                    selections.size - 1,
                                    selections.first().getBestName(),
                                    selections.size - 1
                            )
                        }
                        _viewEvents.post(InviteUsersToRoomViewEvents.Success(successMessage))
                    }
        }
    }

    fun getUserIdsOfRoomMembers(): Set<String> {
        return room.roomSummary()?.otherMemberIds?.toSet().orEmpty()
    }
}
