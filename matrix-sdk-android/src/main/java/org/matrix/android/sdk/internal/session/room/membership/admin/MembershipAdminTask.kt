

package org.matrix.android.sdk.internal.session.room.membership.admin

import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface MembershipAdminTask : Task<MembershipAdminTask.Params, Unit> {

    enum class Type {
        BAN,
        UNBAN,
        KICK
    }

    data class Params(
            val type: Type,
            val roomId: String,
            val userId: String,
            val reason: String?
    )
}

internal class DefaultMembershipAdminTask @Inject constructor(private val roomAPI: RoomAPI) : MembershipAdminTask {

    override suspend fun execute(params: MembershipAdminTask.Params) {
        val userIdAndReason = UserIdAndReason(params.userId, params.reason)
        executeRequest(null) {
            when (params.type) {
                MembershipAdminTask.Type.BAN   -> roomAPI.ban(params.roomId, userIdAndReason)
                MembershipAdminTask.Type.UNBAN -> roomAPI.unban(params.roomId, userIdAndReason)
                MembershipAdminTask.Type.KICK  -> roomAPI.kick(params.roomId, userIdAndReason)
            }
        }
    }
}
