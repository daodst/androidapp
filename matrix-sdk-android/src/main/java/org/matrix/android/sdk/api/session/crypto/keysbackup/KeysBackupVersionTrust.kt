

package org.matrix.android.sdk.api.session.crypto.keysbackup


data class KeysBackupVersionTrust(
        
        val usable: Boolean,

        
        val signatures: List<KeysBackupVersionTrustSignature> = emptyList()
)
