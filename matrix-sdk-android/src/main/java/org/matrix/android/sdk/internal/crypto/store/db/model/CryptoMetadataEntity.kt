

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.matrix.android.sdk.internal.crypto.store.db.deserializeFromRealm
import org.matrix.android.sdk.internal.crypto.store.db.serializeForRealm
import org.matrix.olm.OlmAccount

internal open class CryptoMetadataEntity(
        
        @PrimaryKey var userId: String? = null,
        
        var deviceId: String? = null,
        
        var olmAccountData: String? = null,
        
        var deviceSyncToken: String? = null,
        
        var globalBlacklistUnverifiedDevices: Boolean = false,
        
        var backupVersion: String? = null,

        
        var deviceKeysSentToServer: Boolean = false,

        var xSignMasterPrivateKey: String? = null,
        var xSignUserPrivateKey: String? = null,
        var xSignSelfSignedPrivateKey: String? = null,
        var keyBackupRecoveryKey: String? = null,
        var keyBackupRecoveryKeyVersion: String? = null

) : RealmObject() {

    
    fun getOlmAccount(): OlmAccount? {
        return deserializeFromRealm(olmAccountData)
    }

    
    fun putOlmAccount(olmAccount: OlmAccount?) {
        olmAccountData = serializeForRealm(olmAccount)
    }
}
