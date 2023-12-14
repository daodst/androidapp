
package org.matrix.android.sdk.internal.database

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import io.realm.Realm
import io.realm.RealmConfiguration
import org.matrix.android.sdk.BuildConfig
import org.matrix.android.sdk.internal.session.securestorage.SecretStoringUtils
import timber.log.Timber
import java.security.SecureRandom
import javax.inject.Inject


internal class RealmKeysUtils @Inject constructor(context: Context,
                                                  private val secretStoringUtils: SecretStoringUtils) {

    private val rng = SecureRandom()

    
    private val sharedPreferences = context.getSharedPreferences("im.vector.matrix.android.keys", Context.MODE_PRIVATE)

    private fun generateKeyForRealm(): ByteArray {
        val keyForRealm = ByteArray(Realm.ENCRYPTION_KEY_LENGTH)
        rng.nextBytes(keyForRealm)
        return keyForRealm
    }

    
    private fun hasKeyForDatabase(alias: String): Boolean {
        return sharedPreferences.contains("${ENCRYPTED_KEY_PREFIX}_$alias")
    }

    
    private fun createAndSaveKeyForDatabase(alias: String): ByteArray {
        val key = generateKeyForRealm()
        val encodedKey = Base64.encodeToString(key, Base64.NO_PADDING)
        val toStore = secretStoringUtils.securelyStoreString(encodedKey, alias)
        sharedPreferences.edit {
            putString("${ENCRYPTED_KEY_PREFIX}_$alias", Base64.encodeToString(toStore, Base64.NO_PADDING))
        }
        return key
    }

    
    private fun extractKeyForDatabase(alias: String): ByteArray {
        val encryptedB64 = sharedPreferences.getString("${ENCRYPTED_KEY_PREFIX}_$alias", null)
        val encryptedKey = Base64.decode(encryptedB64, Base64.NO_PADDING)
        val b64 = secretStoringUtils.loadSecureSecret(encryptedKey, alias)
        return Base64.decode(b64, Base64.NO_PADDING)
    }

    fun configureEncryption(realmConfigurationBuilder: RealmConfiguration.Builder, alias: String) {
        val key = getRealmEncryptionKey(alias)

        realmConfigurationBuilder.encryptionKey(key)
    }

    
    fun getRealmEncryptionKey(alias: String): ByteArray {
        val key = if (hasKeyForDatabase(alias)) {
            Timber.i("Found key for alias:$alias")
            extractKeyForDatabase(alias)
        } else {
            Timber.i("Create key for DB alias:$alias")
            createAndSaveKeyForDatabase(alias)
        }

        if (BuildConfig.LOG_PRIVATE_DATA) {
            val log = key.joinToString("") { "%02x".format(it) }
            Timber.w("Database key for alias `$alias`: $log")
        }

        return key
    }

    
    fun clear(alias: String) {
        if (hasKeyForDatabase(alias)) {
            secretStoringUtils.safeDeleteKey(alias)

            sharedPreferences.edit {
                remove("${ENCRYPTED_KEY_PREFIX}_$alias")
            }
        }
    }

    companion object {
        private const val ENCRYPTED_KEY_PREFIX = "REALM_ENCRYPTED_KEY"
    }
}
