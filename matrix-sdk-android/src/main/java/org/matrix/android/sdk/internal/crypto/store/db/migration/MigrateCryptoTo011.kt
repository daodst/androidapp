

package org.matrix.android.sdk.internal.crypto.store.db.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.crypto.store.db.model.CryptoMetadataEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateCryptoTo011(realm: DynamicRealm) : RealmMigrator(realm, 11) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.get("CryptoMetadataEntity")
                ?.addField(CryptoMetadataEntityFields.DEVICE_KEYS_SENT_TO_SERVER, Boolean::class.java)
    }
}
