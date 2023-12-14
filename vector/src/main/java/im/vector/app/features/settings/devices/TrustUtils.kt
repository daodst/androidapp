

package im.vector.app.features.settings.devices

import org.matrix.android.sdk.api.session.crypto.crosssigning.DeviceTrustLevel
import org.matrix.android.sdk.api.session.crypto.model.RoomEncryptionTrustLevel

object TrustUtils {

    fun shieldForTrust(currentDevice: Boolean,
                       trustMSK: Boolean,
                       legacyMode: Boolean,
                       deviceTrustLevel: DeviceTrustLevel?): RoomEncryptionTrustLevel {
        return when {
            currentDevice -> {
                if (legacyMode) {
                    
                    RoomEncryptionTrustLevel.Trusted
                } else {
                    
                    if (trustMSK) {
                        RoomEncryptionTrustLevel.Trusted
                    } else {
                        RoomEncryptionTrustLevel.Warning
                    }
                }
            }
            else          -> {
                if (legacyMode) {
                    
                    if (deviceTrustLevel?.locallyVerified == true) {
                        RoomEncryptionTrustLevel.Trusted
                    } else {
                        RoomEncryptionTrustLevel.Warning
                    }
                } else {
                    if (trustMSK) {
                        
                        when {
                            deviceTrustLevel?.crossSigningVerified == true -> RoomEncryptionTrustLevel.Trusted

                            deviceTrustLevel?.locallyVerified == true      -> RoomEncryptionTrustLevel.Default
                            else                                           -> RoomEncryptionTrustLevel.Warning
                        }
                    } else {
                        
                        
                        RoomEncryptionTrustLevel.Default
                    }
                }
            }
        }
    }
}
