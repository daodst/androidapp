

package org.matrix.android.sdk.api.session.utils

import org.matrix.android.sdk.api.session.utils.bean.BaseResponse
import org.matrix.android.sdk.api.session.utils.bean.EvmosTotalPledgeBean2
import org.matrix.android.sdk.api.session.utils.bean.MediaInfo
import org.matrix.android.sdk.api.session.utils.bean.MyRoomList
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone
import org.matrix.android.sdk.api.session.utils.bean.UserInfoResponse
import org.matrix.android.sdk.api.session.utils.bean.UserLevel
import org.matrix.android.sdk.api.session.utils.param.InviteRoomPayParam
import org.matrix.android.sdk.api.session.utils.param.UserByPhoneParam
import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url


internal interface UtilsAPI {

    
    @GET
    suspend fun getPledgeBean(@Url path: String): EvmosTotalPledgeBean2

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "new/get/user/by/phone")
    suspend fun searchUserByPhone(@Body body: UserByPhoneParam): UserByPhone

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "new/get/user/by/local")
    suspend fun checkInviteRoomPayStatus(@Body body: InviteRoomPayParam): UserByPhone

    
    @GET
    suspend fun getLevel(@Url path: String): BaseResponse<UserLevel>

    @GET
    suspend fun hasUserInfo(@Url path: String): UserInfoResponse

    @GET(NetworkConstants.URI_API_MEDIA_PREFIX_PATH_R0 + "media_info")
    suspend fun getMediaInfo(): MediaInfo

    @GET
    suspend fun getUserLimitByAddr(@Url path: String): Int

    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "room/{roomId}/owner")
    suspend fun getRoomOwner(@Path("roomId") roomId: String): String

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "user/online/active")
    suspend fun isTodayActive(): Boolean

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "user/online_recent_day/active")
    suspend fun recent7DayActive(): String

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "joined_rooms")
    suspend fun getMyJoinedRooms(): MyRoomList?

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "cluster_rooms/{roomId}/join")
    suspend fun httpAutoJoinRoom(@Path("roomId") roomId: String) : Any?


}
