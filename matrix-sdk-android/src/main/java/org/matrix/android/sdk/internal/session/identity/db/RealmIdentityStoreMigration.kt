

package org.matrix.android.sdk.internal.session.identity.db

import io.realm.DynamicRealm
import io.realm.RealmMigration
import org.matrix.android.sdk.internal.session.identity.db.migration.MigrateIdentityTo001
import timber.log.Timber
import javax.inject.Inject

internal class RealmIdentityStoreMigration @Inject constructor() : RealmMigration {
    
    override fun equals(other: Any?) = other is RealmIdentityStoreMigration
    override fun hashCode() = 3000

    val schemaVersion = 1L

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        Timber.d("Migrating Realm Identity from $oldVersion to $newVersion")

        if (oldVersion < 1) MigrateIdentityTo001(realm).perform()
    }
}
