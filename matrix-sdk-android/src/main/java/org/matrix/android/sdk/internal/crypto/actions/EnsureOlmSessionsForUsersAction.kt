

package org.matrix.android.sdk.internal.crypto.actions

import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap
import org.matrix.android.sdk.internal.crypto.MXOlmDevice
import org.matrix.android.sdk.internal.crypto.model.MXOlmSessionResult
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import timber.log.Timber
import javax.inject.Inject

internal class EnsureOlmSessionsForUsersAction @Inject constructor(private val olmDevice: MXOlmDevice,
                                                                   private val cryptoStore: IMXCryptoStore,
                                                                   private val ensureOlmSessionsForDevicesAction: EnsureOlmSessionsForDevicesAction) {

    
    suspend fun handle(users: List<String>): MXUsersDevicesMap<MXOlmSessionResult> {
        Timber.v("## ensureOlmSessionsForUsers() : ensureOlmSessionsForUsers $users")
        val devicesByUser = users.associateWith { userId ->
            val devices = cryptoStore.getUserDevices(userId)?.values.orEmpty()

            devices.filter {
                
                it.identityKey() != olmDevice.deviceCurve25519Key &&
                        
                        !(it.trustLevel?.isVerified() ?: false)
            }
        }
        return ensureOlmSessionsForDevicesAction.handle(devicesByUser)
    }
}
