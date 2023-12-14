

package org.matrix.android.sdk.api.session.utils.model

import org.matrix.android.sdk.api.session.utils.UtilsAPI
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone
import org.matrix.android.sdk.api.session.utils.param.InviteRoomPayParam
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface InviteRoomPayStatusTask : Task<InviteRoomPayStatusTask.Params, UserByPhone> {

    data class Params(
            
            val invitee: String,
            
            val pub_key: String,
            
            val query_sign: String,
            
            val timestamp: String,
            
            val localpart: String,
    )
}

internal class DefaultInviteRoomPayStatusTask @Inject constructor(
        private val utilsAPI: UtilsAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : InviteRoomPayStatusTask {
    override suspend fun execute(params: InviteRoomPayStatusTask.Params): UserByPhone {
        val response = executeRequest(globalErrorReceiver) {
            utilsAPI.checkInviteRoomPayStatus(InviteRoomPayParam(params.invitee, params.pub_key, params.query_sign, params.timestamp, params.localpart))
        }
        return response
    }
}
