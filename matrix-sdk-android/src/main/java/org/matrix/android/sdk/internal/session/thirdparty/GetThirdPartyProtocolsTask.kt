

package org.matrix.android.sdk.internal.session.thirdparty

import org.matrix.android.sdk.api.session.room.model.thirdparty.ThirdPartyProtocol
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetThirdPartyProtocolsTask : Task<Unit, Map<String, ThirdPartyProtocol>>

internal class DefaultGetThirdPartyProtocolsTask @Inject constructor(
        private val thirdPartyAPI: ThirdPartyAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetThirdPartyProtocolsTask {

    override suspend fun execute(params: Unit): Map<String, ThirdPartyProtocol> {
        return executeRequest(globalErrorReceiver) {
            thirdPartyAPI.thirdPartyProtocols()
        }
    }
}
