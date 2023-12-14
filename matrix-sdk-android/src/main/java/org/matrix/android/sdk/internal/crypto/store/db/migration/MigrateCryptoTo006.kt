

package org.matrix.android.sdk.internal.crypto.store.db.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.crypto.store.db.model.CryptoMetadataEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator
import timber.log.Timber

internal class MigrateCryptoTo006(realm: DynamicRealm) : RealmMigrator(realm, 6) {

    override fun doMigrate(realm: DynamicRealm) {
        Timber.d("Updating CryptoMetadataEntity table")
        realm.schema.get("CryptoMetadataEntity")
                ?.addField(CryptoMetadataEntityFields.KEY_BACKUP_RECOVERY_KEY, String::class.java)
                ?.addField(CryptoMetadataEntityFields.KEY_BACKUP_RECOVERY_KEY_VERSION, String::class.java)
    }
}
