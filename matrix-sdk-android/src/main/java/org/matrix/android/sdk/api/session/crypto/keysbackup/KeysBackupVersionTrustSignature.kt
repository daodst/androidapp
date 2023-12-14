

package org.matrix.android.sdk.api.session.crypto.keysbackup

import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo


data class KeysBackupVersionTrustSignature(
        
        val deviceId: String?,

        
        val device: CryptoDeviceInfo?,

        
        val valid: Boolean,
)
