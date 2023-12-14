

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo002(realm: DynamicRealm) : RealmMigrator(realm, 2) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.get("HomeServerCapabilitiesEntity")
                ?.addField("adminE2EByDefault", Boolean::class.java)
                ?.transform { obj ->
                    obj.setBoolean("adminE2EByDefault", true)
                }
    }
}
