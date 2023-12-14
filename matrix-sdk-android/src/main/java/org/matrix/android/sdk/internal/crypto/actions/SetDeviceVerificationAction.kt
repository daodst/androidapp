

package org.matrix.android.sdk.internal.crypto.actions

import org.matrix.android.sdk.api.session.crypto.crosssigning.DeviceTrustLevel
import org.matrix.android.sdk.internal.crypto.keysbackup.DefaultKeysBackupService
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.di.UserId
import timber.log.Timber
import javax.inject.Inject

internal class SetDeviceVerificationAction @Inject constructor(
        private val cryptoStore: IMXCryptoStore,
        @UserId private val userId: String,
        private val defaultKeysBackupService: DefaultKeysBackupService) {

    fun handle(trustLevel: DeviceTrustLevel, userId: String, deviceId: String) {
        val device = cryptoStore.getUserDevice(userId, deviceId)

        
        if (null == device) {
            Timber.w("## setDeviceVerification() : Unknown device $userId:$deviceId")
            return
        }

        if (device.isVerified != trustLevel.isVerified()) {
            if (userId == this.userId) {
                
                
                
                defaultKeysBackupService.checkAndStartKeysBackup()
            }
        }

        if (device.trustLevel != trustLevel) {
            device.trustLevel = trustLevel
            cryptoStore.setDeviceTrust(userId, deviceId, trustLevel.crossSigningVerified, trustLevel.locallyVerified)
        }
    }
}
