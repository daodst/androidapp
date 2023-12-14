

package org.matrix.android.sdk.api.session.utils

import org.matrix.android.sdk.api.session.utils.bean.EvmosTotalPledgeBean2
import org.matrix.android.sdk.api.session.utils.bean.MediaInfo
import org.matrix.android.sdk.api.session.utils.bean.MyRoomList
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone


interface UtilsService {

    
    suspend fun getPledgeBean(account: String): EvmosTotalPledgeBean2

    
    suspend fun searchUserByPhone(phone: String, pub_key: String, query_sign: String, timestamp: String, localpart: String): UserByPhone

    
    suspend fun checkInviteRoomPayStatus(invitee: String, pub_key: String, query_sign: String, timestamp: String, localpart: String): UserByPhone

    
    suspend fun getLevel(id: String): Int

    
    suspend fun hasUserInfo(id: String): Boolean

    
    suspend fun getMediaInfo(): MediaInfo

    
    suspend fun getUserLimitByAddr(uid: String): Int
    suspend fun getRoomOwner(roomId: String): String

    
    suspend fun isTodayActive(): Boolean
    
    suspend fun recent7DayActive(): String
    
    suspend fun httpGetMyJoinedRooms(): MyRoomList?
    
    suspend fun httpAutoJoinRoom(roomId: String) : Any?
}
