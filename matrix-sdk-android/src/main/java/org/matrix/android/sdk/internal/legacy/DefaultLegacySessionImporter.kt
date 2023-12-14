

package org.matrix.android.sdk.internal.legacy

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.runBlocking
import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.auth.data.DiscoveryInformation
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.api.auth.data.SessionParams
import org.matrix.android.sdk.api.auth.data.WellKnownBaseConfig
import org.matrix.android.sdk.api.legacy.LegacySessionImporter
import org.matrix.android.sdk.api.network.ssl.Fingerprint
import org.matrix.android.sdk.api.util.md5
import org.matrix.android.sdk.internal.auth.SessionParamsStore
import org.matrix.android.sdk.internal.crypto.store.db.RealmCryptoStoreMigration
import org.matrix.android.sdk.internal.crypto.store.db.RealmCryptoStoreModule
import org.matrix.android.sdk.internal.database.RealmKeysUtils
import org.matrix.android.sdk.internal.legacy.riot.LoginStorage
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import org.matrix.android.sdk.internal.legacy.riot.Fingerprint as LegacyFingerprint
import org.matrix.android.sdk.internal.legacy.riot.HomeServerConnectionConfig as LegacyHomeServerConnectionConfig

internal class DefaultLegacySessionImporter @Inject constructor(
        private val context: Context,
        private val sessionParamsStore: SessionParamsStore,
        private val realmKeysUtils: RealmKeysUtils,
        private val realmCryptoStoreMigration: RealmCryptoStoreMigration
) : LegacySessionImporter {

    private val loginStorage = LoginStorage(context)

    companion object {
        
        private var DELETE_PREVIOUS_DATA = true
    }

    override fun process(): Boolean {
        Timber.d("Migration: Importing legacy session")

        val list = loginStorage.credentialsList

        Timber.d("Migration: found ${list.size} session(s).")

        val legacyConfig = list.firstOrNull() ?: return false

        runBlocking {
            Timber.d("Migration: importing a session")
            try {
                importCredentials(legacyConfig)
            } catch (t: Throwable) {
                
                Timber.e(t, "Migration: Error importing credential")
            }

            Timber.d("Migration: importing crypto DB")
            try {
                importCryptoDb(legacyConfig)
            } catch (t: Throwable) {
                
                Timber.e(t, "Migration: Error importing crypto DB")
            }

            if (DELETE_PREVIOUS_DATA) {
                try {
                    Timber.d("Migration: clear file system")
                    clearFileSystem(legacyConfig)
                } catch (t: Throwable) {
                    Timber.e(t, "Migration: Error clearing filesystem")
                }
                try {
                    Timber.d("Migration: clear shared prefs")
                    clearSharedPrefs()
                } catch (t: Throwable) {
                    Timber.e(t, "Migration: Error clearing shared prefs")
                }
            } else {
                Timber.d("Migration: clear file system - DEACTIVATED")
                Timber.d("Migration: clear shared prefs - DEACTIVATED")
            }
        }

        
        return true
    }

    private suspend fun importCredentials(legacyConfig: LegacyHomeServerConnectionConfig) {
        @Suppress("DEPRECATION")
        val sessionParams = SessionParams(
                credentials = Credentials(
                        userId = legacyConfig.credentials.userId,
                        accessToken = legacyConfig.credentials.accessToken,
                        refreshToken = legacyConfig.credentials.refreshToken,
                        homeServer = legacyConfig.credentials.homeServer,
                        deviceId = legacyConfig.credentials.deviceId,
                        discoveryInformation = legacyConfig.credentials.wellKnown?.let { wellKnown ->
                            
                            if (wellKnown.homeServer?.baseURL != null || wellKnown.identityServer?.baseURL != null) {
                                DiscoveryInformation(
                                        homeServer = wellKnown.homeServer?.baseURL?.let { WellKnownBaseConfig(baseURL = it) },
                                        identityServer = wellKnown.identityServer?.baseURL?.let { WellKnownBaseConfig(baseURL = it) }
                                )
                            } else {
                                null
                            }
                        }
                ),
                homeServerConnectionConfig = HomeServerConnectionConfig(
                        homeServerUri = legacyConfig.homeserverUri,
                        identityServerUri = legacyConfig.identityServerUri,
                        antiVirusServerUri = legacyConfig.antiVirusServerUri,
                        allowedFingerprints = legacyConfig.allowedFingerprints.map {
                            Fingerprint(
                                    bytes = it.bytes,
                                    hashType = when (it.type) {
                                        LegacyFingerprint.HashType.SHA1,
                                        null                              -> Fingerprint.HashType.SHA1
                                        LegacyFingerprint.HashType.SHA256 -> Fingerprint.HashType.SHA256
                                    }
                            )
                        },
                        shouldPin = legacyConfig.shouldPin(),
                        tlsVersions = legacyConfig.acceptedTlsVersions,
                        tlsCipherSuites = legacyConfig.acceptedTlsCipherSuites,
                        shouldAcceptTlsExtensions = legacyConfig.shouldAcceptTlsExtensions(),
                        allowHttpExtension = false, 
                        forceUsageTlsVersions = legacyConfig.forceUsageOfTlsVersions()
                ),
                
                isTokenValid = true
        )

        Timber.d("Migration: save session")
        sessionParamsStore.save(sessionParams)
    }

    private fun importCryptoDb(legacyConfig: LegacyHomeServerConnectionConfig) {
        
        val userMd5 = legacyConfig.credentials.userId.md5()

        val sessionId = legacyConfig.credentials.let { (if (it.deviceId.isNullOrBlank()) it.userId else "${it.userId}|${it.deviceId}").md5() }
        val newLocation = File(context.filesDir, sessionId)

        val keyAlias = "crypto_module_$userMd5"

        
        newLocation.deleteRecursively()
        newLocation.mkdirs()

        Timber.d("Migration: create legacy realm configuration")

        val realmConfiguration = RealmConfiguration.Builder()
                .directory(File(context.filesDir, userMd5))
                .name("crypto_store.realm")
                .modules(RealmCryptoStoreModule())
                .schemaVersion(realmCryptoStoreMigration.schemaVersion)
                .migration(realmCryptoStoreMigration)
                .build()

        Timber.d("Migration: copy DB to encrypted DB")
        Realm.getInstance(realmConfiguration).use {
            
            it.writeEncryptedCopyTo(File(newLocation, realmConfiguration.realmFileName), realmKeysUtils.getRealmEncryptionKey(keyAlias))
        }
    }

    
    private fun clearFileSystem(legacyConfig: LegacyHomeServerConnectionConfig) {
        val cryptoFolder = legacyConfig.credentials.userId.md5()

        listOf(
                
                File(context.filesDir, "MXFileStore"),
                
                File(context.filesDir, "MXFileCryptoStore"),
                
                File(context.filesDir, "MXLatestMessagesStore"),
                
                File(context.filesDir, "MXMediaStore"),
                File(context.filesDir, "MXMediaStore2"),
                File(context.filesDir, "MXMediaStore3"),
                
                File(context.filesDir, "ext_share"),
                
                File(context.filesDir, cryptoFolder)
        ).forEach { file ->
            try {
                file.deleteRecursively()
            } catch (t: Throwable) {
                Timber.e(t, "Migration: unable to delete $file")
            }
        }
    }

    private fun clearSharedPrefs() {
        
        listOf(
                "Vector.LoginStorage",
                "GcmRegistrationManager",
                "IntegrationManager.Storage"
        ).forEach { prefName ->
            context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()
        }
    }
}
