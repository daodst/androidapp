

package org.matrix.android.sdk.internal.crypto.tasks

import org.matrix.android.sdk.api.session.crypto.model.DevicesListResponse
import org.matrix.android.sdk.internal.crypto.api.CryptoApi
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetDevicesTask : Task<Unit, DevicesListResponse>

internal class DefaultGetDevicesTask @Inject constructor(
        private val cryptoApi: CryptoApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetDevicesTask {

    override suspend fun execute(params: Unit): DevicesListResponse {
        return executeRequest(globalErrorReceiver) {
            cryptoApi.getDevices()
        }
    }
}
