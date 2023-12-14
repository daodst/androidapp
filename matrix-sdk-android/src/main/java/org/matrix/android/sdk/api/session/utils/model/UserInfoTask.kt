

package org.matrix.android.sdk.api.session.utils.model

import android.text.TextUtils
import org.matrix.android.sdk.api.session.utils.UtilsAPI
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject


internal interface UserInfoTask : Task<UserInfoTask.Params, Boolean> {

    data class Params(
            val account: String,
    )
}

internal class DefaultUserInfoTask @Inject constructor(
        private val utilsAPI: UtilsAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : UserInfoTask {
    override suspend fun execute(params: UserInfoTask.Params): Boolean {
        val response = executeRequest(globalErrorReceiver) {
            val url: String = UtilsRpcUrl.getUrl() + "chat/userinfo?account=${params.account}"
            utilsAPI.hasUserInfo(url)
        }
        if (response.status == 0) {
            throw RuntimeException(response.info)
        }
        return (null != response.data) && !TextUtils.isEmpty(response.data.from_address)
    }
}
