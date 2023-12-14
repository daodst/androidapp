

package org.matrix.android.sdk.api.session.crypto.keysbackup


data class MegolmBackupCreationInfo(
        
        val algorithm: String,

        
        val authData: MegolmBackupAuthData,

        
        val recoveryKey: String
)
