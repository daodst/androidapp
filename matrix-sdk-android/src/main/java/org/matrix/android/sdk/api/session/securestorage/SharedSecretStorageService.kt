

package org.matrix.android.sdk.api.session.securestorage

import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.session.crypto.crosssigning.KEYBACKUP_SECRET_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.MASTER_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.SELF_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.USER_SIGNING_KEY_SSSS_NAME



interface SharedSecretStorageService {

    
    suspend fun generateKey(keyId: String,
                            key: SsssKeySpec?,
                            keyName: String,
                            keySigner: KeySigner?): SsssKeyCreationInfo

    
    suspend fun generateKeyWithPassphrase(keyId: String,
                                          keyName: String,
                                          passphrase: String,
                                          keySigner: KeySigner,
                                          progressListener: ProgressListener?): SsssKeyCreationInfo

    fun getKey(keyId: String): KeyInfoResult

    
    fun getDefaultKey(): KeyInfoResult

    suspend fun setDefaultKey(keyId: String)

    
    fun hasKey(keyId: String): Boolean

    
    suspend fun storeSecret(name: String, secretBase64: String, keys: List<KeyRef>)

    
    fun getAlgorithmsForSecret(name: String): List<KeyInfoResult>

    
    suspend fun getSecret(name: String, keyId: String?, secretKey: SsssKeySpec): String

    
    fun isRecoverySetup(): Boolean {
        return checkShouldBeAbleToAccessSecrets(
                secretNames = listOf(MASTER_KEY_SSSS_NAME, USER_SIGNING_KEY_SSSS_NAME, SELF_SIGNING_KEY_SSSS_NAME),
                keyId = null
        ) is IntegrityResult.Success
    }

    fun isMegolmKeyInBackup(): Boolean {
        return checkShouldBeAbleToAccessSecrets(
                secretNames = listOf(KEYBACKUP_SECRET_SSSS_NAME),
                keyId = null
        ) is IntegrityResult.Success
    }

    fun checkShouldBeAbleToAccessSecrets(secretNames: List<String>, keyId: String?): IntegrityResult

    fun requestSecret(name: String, myOtherDeviceId: String)

    data class KeyRef(
            val keyId: String?,
            val keySpec: SsssKeySpec?
    )
}
