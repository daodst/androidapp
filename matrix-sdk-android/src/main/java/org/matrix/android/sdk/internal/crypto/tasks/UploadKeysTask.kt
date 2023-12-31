

package org.matrix.android.sdk.internal.crypto.tasks

import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.crypto.api.CryptoApi
import org.matrix.android.sdk.internal.crypto.model.rest.DeviceKeys
import org.matrix.android.sdk.internal.crypto.model.rest.KeysUploadBody
import org.matrix.android.sdk.internal.crypto.model.rest.KeysUploadResponse
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import timber.log.Timber
import javax.inject.Inject

internal interface UploadKeysTask : Task<UploadKeysTask.Params, KeysUploadResponse> {
    data class Params(
            
            val deviceKeys: DeviceKeys?,
            
            val oneTimeKeys: JsonDict?,
            val fallbackKeys: JsonDict?
    )
}

internal class DefaultUploadKeysTask @Inject constructor(
        private val cryptoApi: CryptoApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : UploadKeysTask {

    override suspend fun execute(params: UploadKeysTask.Params): KeysUploadResponse {
        val body = KeysUploadBody(
                deviceKeys = params.deviceKeys,
                oneTimeKeys = params.oneTimeKeys,
                fallbackKeys = params.fallbackKeys
        )

        Timber.i("## Uploading device keys -> $body")

        return executeRequest(globalErrorReceiver) {
            cryptoApi.uploadKeys(body)
        }
    }
}
