

package org.matrix.android.sdk.internal.crypto.keysbackup.model.rest

import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM_BACKUP
import org.matrix.android.sdk.api.session.crypto.keysbackup.MegolmBackupAuthData
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.di.MoshiProvider


internal interface KeysAlgorithmAndData {

    
    val algorithm: String

    
    val authData: JsonDict

    
    fun getAuthDataAsMegolmBackupAuthData(): MegolmBackupAuthData? {
        return MoshiProvider.providesMoshi()
                .takeIf { algorithm == MXCRYPTO_ALGORITHM_MEGOLM_BACKUP }
                ?.adapter(MegolmBackupAuthData::class.java)
                ?.fromJsonValue(authData)
    }
}
