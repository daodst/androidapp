

package org.matrix.android.sdk.api.session.space

import android.net.Uri
import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.RoomSortOrder
import org.matrix.android.sdk.api.session.room.RoomSummaryQueryParams
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.space.peeking.SpacePeekResult

typealias SpaceSummaryQueryParams = RoomSummaryQueryParams

interface SpaceService {

    
    suspend fun createSpace(params: CreateSpaceParams): String

    
    suspend fun createSpace(name: String,
                            topic: String?,
                            avatarUri: Uri?,
                            isPublic: Boolean,
                            roomAliasLocalPart: String? = null): String

    
    fun getSpace(spaceId: String): Space?

    
    suspend fun peekSpace(spaceId: String): SpacePeekResult

    
    suspend fun querySpaceChildren(spaceId: String,
                                   suggestedOnly: Boolean? = null,
                                   limit: Int? = null,
                                   from: String? = null,
            
                                   knownStateList: List<Event>? = null): SpaceHierarchyData

    
    fun getSpaceSummariesLive(queryParams: SpaceSummaryQueryParams,
                              sortOrder: RoomSortOrder = RoomSortOrder.NONE): LiveData<List<RoomSummary>>

    fun getSpaceSummaries(spaceSummaryQueryParams: SpaceSummaryQueryParams,
                          sortOrder: RoomSortOrder = RoomSortOrder.NONE): List<RoomSummary>

    suspend fun joinSpace(spaceIdOrAlias: String,
                          reason: String? = null,
                          viaServers: List<String> = emptyList()): JoinSpaceResult

    suspend fun rejectInvite(spaceId: String, reason: String?)

    
    suspend fun leaveSpace(spaceId: String, reason: String? = null)


    
    suspend fun setSpaceParent(childRoomId: String, parentSpaceId: String, canonical: Boolean, viaServers: List<String>)

    suspend fun removeSpaceParent(childRoomId: String, parentSpaceId: String)

    fun getRootSpaceSummaries(): List<RoomSummary>
}
