

package org.matrix.android.sdk.api.session.utils.model

import org.matrix.android.sdk.api.session.utils.UtilsAPI
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone
import org.matrix.android.sdk.api.session.utils.param.UserByPhoneParam
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface SearchUserByPhoneTask : Task<SearchUserByPhoneTask.Params, UserByPhone> {

    data class Params(
            val phone: String,
            
            val pub_key: String,
            
            val query_sign: String,
            
            val timestamp: String,
            
            val localpart: String,
    )
}

internal class DefaultSearchUserByPhoneTask @Inject constructor(
        private val utilsAPI: UtilsAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : SearchUserByPhoneTask {
    override suspend fun execute(params: SearchUserByPhoneTask.Params): UserByPhone {
        val response = executeRequest(globalErrorReceiver) {
            utilsAPI.searchUserByPhone(UserByPhoneParam(params.phone, params.pub_key, params.query_sign, params.timestamp, params.localpart))
        }
        return response
    }
}
