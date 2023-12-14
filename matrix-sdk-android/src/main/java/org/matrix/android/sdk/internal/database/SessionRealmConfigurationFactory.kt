

package org.matrix.android.sdk.internal.database

import android.content.Context
import androidx.core.content.edit
import io.realm.Realm
import io.realm.RealmConfiguration
import org.matrix.android.sdk.internal.database.model.SessionRealmModule
import org.matrix.android.sdk.internal.di.SessionFilesDirectory
import org.matrix.android.sdk.internal.di.SessionId
import org.matrix.android.sdk.internal.di.UserMd5
import org.matrix.android.sdk.internal.session.SessionModule
import timber.log.Timber
import java.io.File
import javax.inject.Inject

private const val REALM_SHOULD_CLEAR_FLAG_ = "REALM_SHOULD_CLEAR_FLAG_"
private const val REALM_NAME = "disk_store.realm"


internal class SessionRealmConfigurationFactory @Inject constructor(
        private val realmKeysUtils: RealmKeysUtils,
        private val realmSessionStoreMigration: RealmSessionStoreMigration,
        @SessionFilesDirectory val directory: File,
        @SessionId val sessionId: String,
        @UserMd5 val userMd5: String,
        context: Context) {

    
    private val sharedPreferences = context.getSharedPreferences("im.vector.matrix.android.realm", Context.MODE_PRIVATE)

    fun create(): RealmConfiguration {
        val shouldClearRealm = sharedPreferences.getBoolean("$REALM_SHOULD_CLEAR_FLAG_$sessionId", false)
        Timber.i("shouldClearRealm=${shouldClearRealm}");
        if (shouldClearRealm) {
            Timber.e("************************************************************")
            Timber.e("The realm file session was corrupted and couldn't be loaded.")
            Timber.e("The file has been deleted to recover.")
            Timber.e("************************************************************")
            deleteRealmFiles()
        }
        sharedPreferences.edit {
            putBoolean("$REALM_SHOULD_CLEAR_FLAG_$sessionId", true)
        }

        val realmConfiguration = RealmConfiguration.Builder()
                .compactOnLaunch()
                .directory(directory)
                .name(REALM_NAME)
                .apply {
                    realmKeysUtils.configureEncryption(this, SessionModule.getKeyAlias(userMd5))
                }
                .allowWritesOnUiThread(true)
                .modules(SessionRealmModule())
                .schemaVersion(realmSessionStoreMigration.schemaVersion)
                .migration(realmSessionStoreMigration)
                .build()

        
        Realm.getInstance(realmConfiguration).use {
            Timber.v("Successfully create realm instance")
            sharedPreferences.edit {
                putBoolean("$REALM_SHOULD_CLEAR_FLAG_$sessionId", false)
            }
        }
        return realmConfiguration
    }

    
    private fun deleteRealmFiles() {

        listOf(REALM_NAME, "$REALM_NAME.lock", "$REALM_NAME.note", "$REALM_NAME.management").forEach { file ->
            try {
                Timber.i("delete file:${file}")
                File(directory, file).deleteRecursively()
            } catch (e: Exception) {
                Timber.e(e, "Unable to delete files")
            }
        }
    }
}
