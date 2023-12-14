

package org.matrix.android.sdk.api.session.utils.model

import org.matrix.android.sdk.api.session.utils.UtilsAPI
import org.matrix.android.sdk.api.session.utils.bean.EvmosTotalPledgeBean2
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface PledgeBeanTask : Task<PledgeBeanTask.Params, EvmosTotalPledgeBean2> {

    data class Params(
            val account: String,
    )
}

internal class DefaultPledgeBeanTask @Inject constructor(
        private val utilsAPI: UtilsAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : PledgeBeanTask {

    override suspend fun execute(params: PledgeBeanTask.Params): EvmosTotalPledgeBean2 {
        val response = executeRequest(globalErrorReceiver) {
            
            val url: String = UtilsRpcUrl.getUrl() + "pledge/all?account=${params.account}"
            utilsAPI.getPledgeBean(url)
        }
        return response
    }
}
