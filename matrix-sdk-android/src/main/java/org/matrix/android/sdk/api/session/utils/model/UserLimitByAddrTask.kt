

package org.matrix.android.sdk.api.session.utils.model

import org.matrix.android.sdk.api.session.utils.UtilsAPI
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface UserLimitByAddrTask : Task<UserLimitByAddrTask.Params, Int> {
    data class Params(
            val uid: String,
    )
}

internal class DefaultUserLimitByAddrTask @Inject constructor(
        private val utilsAPI: UtilsAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : UserLimitByAddrTask {
    override suspend fun execute(params: UserLimitByAddrTask.Params): Int {
        val response = executeRequest(globalErrorReceiver) {
            val url: String = UtilsRpcUrl.getChat23478() + "turn/getUserLimitByAddr?addr=${params.uid}"
            utilsAPI.getUserLimitByAddr(url)
        }
        return response
    }
}
