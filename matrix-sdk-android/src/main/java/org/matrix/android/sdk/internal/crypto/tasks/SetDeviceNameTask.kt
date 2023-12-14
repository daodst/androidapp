

package org.matrix.android.sdk.internal.crypto.tasks

import org.matrix.android.sdk.internal.crypto.api.CryptoApi
import org.matrix.android.sdk.internal.crypto.model.rest.UpdateDeviceInfoBody
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface SetDeviceNameTask : Task<SetDeviceNameTask.Params, Unit> {
    data class Params(
            
            val deviceId: String,
            
            val deviceName: String
    )
}

internal class DefaultSetDeviceNameTask @Inject constructor(
        private val cryptoApi: CryptoApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : SetDeviceNameTask {

    override suspend fun execute(params: SetDeviceNameTask.Params) {
        val body = UpdateDeviceInfoBody(
                displayName = params.deviceName
        )
        return executeRequest(globalErrorReceiver) {
            cryptoApi.updateDeviceInfo(params.deviceId, body)
        }
    }
}
