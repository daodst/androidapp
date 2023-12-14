

package org.matrix.android.sdk.internal.crypto.store.db

import io.realm.DynamicRealm
import io.realm.RealmMigration
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo001Legacy
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo002Legacy
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo003RiotX
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo004
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo005
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo006
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo007
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo008
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo009
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo010
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo011
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo012
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo013
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo014
import org.matrix.android.sdk.internal.crypto.store.db.migration.MigrateCryptoTo015
import timber.log.Timber
import javax.inject.Inject

internal class RealmCryptoStoreMigration @Inject constructor() : RealmMigration {
    
    override fun equals(other: Any?) = other is RealmCryptoStoreMigration
    override fun hashCode() = 5000

    
    
    
    val schemaVersion = 15L

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        Timber.d("Migrating Realm Crypto from $oldVersion to $newVersion")

        if (oldVersion < 1) MigrateCryptoTo001Legacy(realm).perform()
        if (oldVersion < 2) MigrateCryptoTo002Legacy(realm).perform()
        if (oldVersion < 3) MigrateCryptoTo003RiotX(realm).perform()
        if (oldVersion < 4) MigrateCryptoTo004(realm).perform()
        if (oldVersion < 5) MigrateCryptoTo005(realm).perform()
        if (oldVersion < 6) MigrateCryptoTo006(realm).perform()
        if (oldVersion < 7) MigrateCryptoTo007(realm).perform()
        if (oldVersion < 8) MigrateCryptoTo008(realm).perform()
        if (oldVersion < 9) MigrateCryptoTo009(realm).perform()
        if (oldVersion < 10) MigrateCryptoTo010(realm).perform()
        if (oldVersion < 11) MigrateCryptoTo011(realm).perform()
        if (oldVersion < 12) MigrateCryptoTo012(realm).perform()
        if (oldVersion < 13) MigrateCryptoTo013(realm).perform()
        if (oldVersion < 14) MigrateCryptoTo014(realm).perform()
        if (oldVersion < 15) MigrateCryptoTo015(realm).perform()
    }
}
