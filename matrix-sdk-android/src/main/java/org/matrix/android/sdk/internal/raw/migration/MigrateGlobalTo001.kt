

package org.matrix.android.sdk.internal.raw.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.KnownServerUrlEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateGlobalTo001(realm: DynamicRealm) : RealmMigrator(realm, 1) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.create("KnownServerUrlEntity")
                .addField(KnownServerUrlEntityFields.URL, String::class.java)
                .addPrimaryKey(KnownServerUrlEntityFields.URL)
                .setRequired(KnownServerUrlEntityFields.URL, true)
    }
}
