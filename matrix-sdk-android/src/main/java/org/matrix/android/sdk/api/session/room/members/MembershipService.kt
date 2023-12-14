

package org.matrix.android.sdk.api.session.room.members

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary


interface MembershipService {

    
    suspend fun loadRoomMembersIfNeeded()
    suspend fun loadRoomMembers(): Map<String, Map<String, String>>

    
    fun getRoomMember(userId: String): RoomMemberSummary?

    
    fun getRoomMembers(queryParams: RoomMemberQueryParams): List<RoomMemberSummary>

    
    fun getRoomMembersLive(queryParams: RoomMemberQueryParams): LiveData<List<RoomMemberSummary>>

    fun getNumberOfJoinedMembers(): Int

    
    suspend fun invite(userId: String, reason: String? = null)

    
    suspend fun invite3pid(threePid: ThreePid)

    
    suspend fun ban(userId: String, reason: String? = null)

    
    suspend fun unban(userId: String, reason: String? = null)

    
    suspend fun remove(userId: String, reason: String? = null)

    @Deprecated("Use remove instead", ReplaceWith("remove(userId, reason)"))
    suspend fun kick(userId: String, reason: String? = null) = remove(userId, reason)
}
