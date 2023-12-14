

package org.matrix.android.sdk.api.session.utils

import org.matrix.android.sdk.api.session.utils.bean.EvmosTotalPledgeBean2
import org.matrix.android.sdk.api.session.utils.bean.MediaInfo
import org.matrix.android.sdk.api.session.utils.bean.MyRoomList
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone
import org.matrix.android.sdk.api.session.utils.model.AutoJoinRoomTask
import org.matrix.android.sdk.api.session.utils.model.GetMyJoinedRoomsTask
import org.matrix.android.sdk.api.session.utils.model.InviteRoomPayStatusTask
import org.matrix.android.sdk.api.session.utils.model.MediaInfoTask
import org.matrix.android.sdk.api.session.utils.model.PledgeBeanTask
import org.matrix.android.sdk.api.session.utils.model.Recent7DayActiveTask
import org.matrix.android.sdk.api.session.utils.model.RoomOwnerTask
import org.matrix.android.sdk.api.session.utils.model.SearchUserByPhoneTask
import org.matrix.android.sdk.api.session.utils.model.TodayIsActiveTask
import org.matrix.android.sdk.api.session.utils.model.UserInfoTask
import org.matrix.android.sdk.api.session.utils.model.UserLevelTask
import org.matrix.android.sdk.api.session.utils.model.UserLimitByAddrTask
import javax.inject.Inject

internal class DefaultUtilsService @Inject constructor(
        private val pledgeBeanTask: PledgeBeanTask,
        private val searchUserByPhoneTask: SearchUserByPhoneTask,
        private val inviteRoomPayStatusTask: InviteRoomPayStatusTask,
        private val userLevelTask: UserLevelTask,
        private val infoTask: UserInfoTask,
        private val mediaInfoTask: MediaInfoTask,
        private val userLimitByAddrTask: UserLimitByAddrTask,
        private val roomOwnerTask: RoomOwnerTask,
        private val todayIsActiveTask: TodayIsActiveTask,
        private val recent7DayActiveTask: Recent7DayActiveTask,
        private val getMyJoinedRoomsTask: GetMyJoinedRoomsTask,
        private val autoJoinRoomTask: AutoJoinRoomTask,
) : UtilsService {
    override suspend fun getPledgeBean(account: String): EvmosTotalPledgeBean2 {
        val params = PledgeBeanTask.Params(account)
        return pledgeBeanTask.execute(params)
    }

    override suspend fun searchUserByPhone(phone: String, pub_key: String, query_sign: String, timestamp: String, localpart: String): UserByPhone {
        val params = SearchUserByPhoneTask.Params(phone, pub_key, query_sign, timestamp, localpart)
        return searchUserByPhoneTask.execute(params)
    }

    override suspend fun checkInviteRoomPayStatus(invitee: String, pub_key: String, query_sign: String, timestamp: String, localpart: String): UserByPhone {
        val params = InviteRoomPayStatusTask.Params(invitee, pub_key, query_sign, timestamp, localpart)
        return inviteRoomPayStatusTask.execute(params)
    }

    override suspend fun getLevel(id: String): Int {
        return userLevelTask.execute(UserLevelTask.Params(id))
    }

    override suspend fun hasUserInfo(id: String): Boolean {
        return infoTask.execute(UserInfoTask.Params(id))
    }

    override suspend fun getMediaInfo(): MediaInfo {
        return mediaInfoTask.execute(Unit)
    }

    override suspend fun getUserLimitByAddr(uid: String): Int {
        return userLimitByAddrTask.execute(UserLimitByAddrTask.Params(uid))
    }

    override suspend fun getRoomOwner(roomId: String): String {
        return roomOwnerTask.execute(RoomOwnerTask.Params(roomId))
    }

    override suspend fun isTodayActive(): Boolean {
        return todayIsActiveTask.execute(null)
    }

    override suspend fun recent7DayActive(): String {
        return recent7DayActiveTask.execute(null)
    }

    override suspend fun httpGetMyJoinedRooms(): MyRoomList? {
        return getMyJoinedRoomsTask.execute(null)
    }

    override suspend fun httpAutoJoinRoom(roomId: String): Any? {
        return autoJoinRoomTask.execute(roomId)
    }
}
