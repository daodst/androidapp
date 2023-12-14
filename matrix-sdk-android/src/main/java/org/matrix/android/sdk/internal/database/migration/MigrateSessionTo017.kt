

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.EventInsertEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo017(realm: DynamicRealm) : RealmMigrator(realm, 17) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.get("EventInsertEntity")
                ?.addField(EventInsertEntityFields.CAN_BE_PROCESSED, Boolean::class.java)
    }
}
