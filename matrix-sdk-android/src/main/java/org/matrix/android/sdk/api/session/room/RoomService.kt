

package org.matrix.android.sdk.api.session.room

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.identity.model.SignInvitationResult
import org.matrix.android.sdk.api.session.room.alias.RoomAliasDescription
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.peeking.PeekResult
import org.matrix.android.sdk.api.session.room.summary.RoomAggregateNotificationCount
import org.matrix.android.sdk.api.util.Optional


interface RoomService {

    
    suspend fun createRoom(createRoomParams: CreateRoomParams): String

    
    suspend fun createDirectRoom(otherUserId: String): String {
        return createRoom(
                CreateRoomParams()
                        .apply {
                            invitedUserIds.add(otherUserId)
                            setDirectMessage()
                            enableEncryptionIfInvitedUsersSupportIt = true
                        }
        )
    }

    
    suspend fun joinRoom(roomIdOrAlias: String,
                         reason: String? = null,
                         viaServers: List<String> = emptyList())

    
    suspend fun joinRoom(
            roomId: String,
            reason: String? = null,
            thirdPartySigned: SignInvitationResult
    )

    
    suspend fun leaveRoom(roomId: String, reason: String? = null)

    
    fun getRoom(roomId: String): Room?

    
    fun getRoomSummary(roomIdOrAlias: String): RoomSummary?

    
    fun getRoomSummaries(queryParams: RoomSummaryQueryParams,
                         sortOrder: RoomSortOrder = RoomSortOrder.NONE): List<RoomSummary>

    
    fun getRoomSummariesLive(queryParams: RoomSummaryQueryParams,
                             sortOrder: RoomSortOrder = RoomSortOrder.ACTIVITY): LiveData<List<RoomSummary>>

    
    fun getBreadcrumbs(queryParams: RoomSummaryQueryParams): List<RoomSummary>

    
    fun getBreadcrumbsLive(queryParams: RoomSummaryQueryParams): LiveData<List<RoomSummary>>

    
    suspend fun onRoomDisplayed(roomId: String)

    
    suspend fun markAllAsRead(roomIds: List<String>)

    
    suspend fun getRoomIdByAlias(roomAlias: String,
                                 searchOnServer: Boolean): Optional<RoomAliasDescription>

    
    suspend fun deleteRoomAlias(roomAlias: String)

    
    fun getChangeMemberships(roomIdOrAlias: String): ChangeMembershipState

    
    fun getChangeMembershipsLive(): LiveData<Map<String, ChangeMembershipState>>

    
    fun getExistingDirectRoomWithUser(otherUserId: String): String?

    
    fun getRoomMember(userId: String, roomId: String): RoomMemberSummary?
    fun getLikeRoomMember(userId: String, roomId: String): RoomMemberSummary?

    
    fun getRoomMemberLive(userId: String, roomId: String): LiveData<Optional<RoomMemberSummary>>

    
    suspend fun getRoomState(roomId: String): List<Event>

    
    suspend fun peekRoom(roomIdOrAlias: String): PeekResult

    
    fun getPagedRoomSummariesLive(queryParams: RoomSummaryQueryParams,
                                  pagedListConfig: PagedList.Config = defaultPagedListConfig,
                                  sortOrder: RoomSortOrder = RoomSortOrder.ACTIVITY): LiveData<PagedList<RoomSummary>>

    
    fun getFilteredPagedRoomSummariesLive(queryParams: RoomSummaryQueryParams,
                                          pagedListConfig: PagedList.Config = defaultPagedListConfig,
                                          sortOrder: RoomSortOrder = RoomSortOrder.ACTIVITY): UpdatableLivePageResult

    fun getAllRoom(queryParams: RoomSummaryQueryParams,
                   sortOrder: RoomSortOrder = RoomSortOrder.ACTIVITY): LiveData<List<RoomSummary>>

    
    fun getRoomCountLive(queryParams: RoomSummaryQueryParams): LiveData<Int>

    
    fun getNotificationCountForRooms(queryParams: RoomSummaryQueryParams): RoomAggregateNotificationCount

    private val defaultPagedListConfig
        get() = PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(20)
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .build()

    fun getFlattenRoomSummaryChildrenOf(spaceId: String?, memberships: List<Membership> = Membership.activeMemberships()): List<RoomSummary>

    
    fun getFlattenRoomSummaryChildrenOfLive(spaceId: String?,
                                            memberships: List<Membership> = Membership.activeMemberships()): LiveData<List<RoomSummary>>

    
    fun refreshJoinedRoomSummaryPreviews(roomId: String?)
}
