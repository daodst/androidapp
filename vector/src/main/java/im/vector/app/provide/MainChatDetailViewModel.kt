

package im.vector.app.provide

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import im.vector.app.AppStateHandler
import im.vector.app.RoomGroupingMethod
import im.vector.app.features.invite.AutoAcceptInvites
import im.vector.app.features.invite.showInvites
import im.vector.lib.core.utils.flow.throttleFirst
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.matrix.android.sdk.api.query.ActiveSpaceFilter
import org.matrix.android.sdk.api.query.RoomCategoryFilter
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.RoomSortOrder
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject


class MainChatDetailViewModel @Inject constructor(
        application: Application,
) : AndroidViewModel(application) {

    val peopleChatNum = MutableLiveData<ChatDataNum>()
    val groupChatNum = MutableLiveData<ChatDataNum>()
    fun observeRoomSummaries(appStateHandler: AppStateHandler, autoAcceptInvites: AutoAcceptInvites, session: Session) {
        appStateHandler.selectedRoomGroupingFlow.distinctUntilChanged().flatMapLatest {
            
            
            session.getPagedRoomSummariesLive(
                    roomSummaryQueryParams {
                        memberships = Membership.activeMemberships()
                    },
                    sortOrder = RoomSortOrder.NONE
            ).asFlow()
        }
                .throttleFirst(300)
                .onEach {
                    val groupingMethod = appStateHandler.getCurrentRoomGroupingMethod()
                    when (groupingMethod) {
                        is RoomGroupingMethod.ByLegacyGroup -> {
                            
                        }
                        is RoomGroupingMethod.BySpace       -> {
                            val activeSpaceRoomId = groupingMethod.spaceSummary?.roomId

                            var newroomsInvite = 0
                            if (autoAcceptInvites.showInvites()) {
                                newroomsInvite = session.getRoomSummaries(
                                        roomSummaryQueryParams {
                                            memberships = listOf(Membership.INVITE)
                                            roomCategoryFilter = RoomCategoryFilter.ALL
                                            activeSpaceFilter = activeSpaceRoomId?.let { ActiveSpaceFilter.ActiveSpace(it) } ?: ActiveSpaceFilter.None
                                        }
                                ).size
                            }
                            
                            val notGroupRooms = session.getNotificationCountForRooms(
                                    roomSummaryQueryParams {
                                        memberships = listOf(Membership.JOIN)
                                        roomCategoryFilter = RoomCategoryFilter.NOT_GROUP
                                        
                                        activeSpaceFilter = ActiveSpaceFilter.ActiveSpace(groupingMethod.spaceSummary?.roomId)
                                    }
                            )

                            val groupRooms = session.getNotificationCountForRooms(
                                    roomSummaryQueryParams {
                                        memberships = listOf(Membership.JOIN)
                                        roomCategoryFilter = RoomCategoryFilter.ONLY_GROUP
                                        activeSpaceFilter = ActiveSpaceFilter.ActiveSpace(groupingMethod.spaceSummary?.roomId)
                                    }
                            )

                            val notificationNormalCountPeople = notGroupRooms.totalCount + newroomsInvite
                            val notificationNormalHighlightPeople = notGroupRooms.totalCount + newroomsInvite > 0
                            val notificationGroupCountPeople = groupRooms.totalCount
                            val notificationGroupHighlightPeople = groupRooms.totalCount > 0

                            peopleChatNum.postValue(ChatDataNum(notificationNormalCountPeople,notificationGroupHighlightPeople))
                            groupChatNum.postValue(ChatDataNum(notificationGroupCountPeople,notificationGroupHighlightPeople))

                            
                        }
                        null                                -> Unit
                    }
                }
                .launchIn(viewModelScope)
    }
}
