

package org.matrix.android.sdk.internal.auth.db

import io.realm.DynamicRealm
import io.realm.RealmMigration
import org.matrix.android.sdk.internal.auth.db.migration.MigrateAuthTo001
import org.matrix.android.sdk.internal.auth.db.migration.MigrateAuthTo002
import org.matrix.android.sdk.internal.auth.db.migration.MigrateAuthTo003
import org.matrix.android.sdk.internal.auth.db.migration.MigrateAuthTo004
import timber.log.Timber
import javax.inject.Inject

internal class AuthRealmMigration @Inject constructor() : RealmMigration {
    
    override fun equals(other: Any?) = other is AuthRealmMigration
    override fun hashCode() = 4000

    val schemaVersion = 4L

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        Timber.d("Migrating Auth Realm from $oldVersion to $newVersion")

        if (oldVersion < 1) MigrateAuthTo001(realm).perform()
        if (oldVersion < 2) MigrateAuthTo002(realm).perform()
        if (oldVersion < 3) MigrateAuthTo003(realm).perform()
        if (oldVersion < 4) MigrateAuthTo004(realm).perform()
    }
}
