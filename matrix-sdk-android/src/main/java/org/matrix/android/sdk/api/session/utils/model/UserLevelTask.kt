

package org.matrix.android.sdk.api.session.utils.model

import org.matrix.android.sdk.api.session.utils.UtilsAPI
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject


internal interface UserLevelTask : Task<UserLevelTask.Params, Int> {

    data class Params(
            val account: String,
    )
}

internal class DefaultUserLevelTask @Inject constructor(
        private val utilsAPI: UtilsAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : UserLevelTask {
    override suspend fun execute(params: UserLevelTask.Params): Int {
        val response = executeRequest(globalErrorReceiver) {
            val url: String = UtilsRpcUrl.getUrl() + "chat/userinfo?account=${params.account}"
            utilsAPI.getLevel(url)
        }
        if (response.status == 0) {
            throw  RuntimeException(response.info)
        }
        return response.data.pledge_level
    }
}
