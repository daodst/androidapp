

package org.matrix.android.sdk.internal.crypto

import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.session.crypto.crosssigning.DeviceTrustLevel
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject

@SessionScope
internal class MyDeviceInfoHolder @Inject constructor(
        
        credentials: Credentials,
        
        cryptoStore: IMXCryptoStore,
        
        olmDevice: MXOlmDevice
) {
    
    
    val myDevice: CryptoDeviceInfo

    init {

        val keys = HashMap<String, String>()

        if (!olmDevice.deviceEd25519Key.isNullOrEmpty()) {
            keys["ed25519:" + credentials.deviceId] = olmDevice.deviceEd25519Key!!
        }

        if (!olmDevice.deviceCurve25519Key.isNullOrEmpty()) {
            keys["curve25519:" + credentials.deviceId] = olmDevice.deviceCurve25519Key!!
        }



        
        
        val crossSigned = cryptoStore.getMyCrossSigningInfo()?.masterKey()?.trustLevel?.locallyVerified ?: false

        myDevice = CryptoDeviceInfo(
                credentials.deviceId!!,
                credentials.userId,
                keys = keys,
                algorithms = MXCryptoAlgorithms.supportedAlgorithms(),
                trustLevel = DeviceTrustLevel(crossSigned, true)
        )

        
        val endToEndDevicesForUser = cryptoStore.getUserDevices(credentials.userId)

        val myDevices = endToEndDevicesForUser.orEmpty().toMutableMap()

        myDevices[myDevice.deviceId] = myDevice

        cryptoStore.storeUserDevices(credentials.userId, myDevices)
    }
}
