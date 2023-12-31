

package org.matrix.android.sdk.internal.crypto.tasks

import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap
import org.matrix.android.sdk.internal.crypto.api.CryptoApi
import org.matrix.android.sdk.internal.crypto.model.MXKey
import org.matrix.android.sdk.internal.crypto.model.rest.KeysClaimBody
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import timber.log.Timber
import javax.inject.Inject

internal interface ClaimOneTimeKeysForUsersDeviceTask : Task<ClaimOneTimeKeysForUsersDeviceTask.Params, MXUsersDevicesMap<MXKey>> {
    data class Params(
            
            val usersDevicesKeyTypesMap: MXUsersDevicesMap<String>
    )
}

internal class DefaultClaimOneTimeKeysForUsersDevice @Inject constructor(
        private val cryptoApi: CryptoApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : ClaimOneTimeKeysForUsersDeviceTask {

    override suspend fun execute(params: ClaimOneTimeKeysForUsersDeviceTask.Params): MXUsersDevicesMap<MXKey> {
        val body = KeysClaimBody(oneTimeKeys = params.usersDevicesKeyTypesMap.map)

        val keysClaimResponse = executeRequest(globalErrorReceiver) {
            cryptoApi.claimOneTimeKeysForUsersDevices(body)
        }
        val map = MXUsersDevicesMap<MXKey>()
        keysClaimResponse.oneTimeKeys?.let { oneTimeKeys ->
            for ((userId, mapByUserId) in oneTimeKeys) {
                for ((deviceId, deviceKey) in mapByUserId) {
                    val mxKey = MXKey.from(deviceKey)

                    if (mxKey != null) {
                        map.setObject(userId, deviceId, mxKey)
                    } else {
                        Timber.e("## claimOneTimeKeysForUsersDevices : fail to create a MXKey")
                    }
                }
            }
        }
        return map
    }
}
