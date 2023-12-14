

package org.matrix.android.sdk.internal.raw

import io.realm.DynamicRealm
import io.realm.RealmMigration
import org.matrix.android.sdk.internal.raw.migration.MigrateGlobalTo001
import timber.log.Timber
import javax.inject.Inject

internal class GlobalRealmMigration @Inject constructor() : RealmMigration {
    
    override fun equals(other: Any?) = other is GlobalRealmMigration
    override fun hashCode() = 2000

    val schemaVersion = 1L

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        Timber.d("Migrating Global Realm from $oldVersion to $newVersion")

        if (oldVersion < 1) MigrateGlobalTo001(realm).perform()
    }
}
