

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo001(realm: DynamicRealm) : RealmMigrator(realm, 1) {

    override fun doMigrate(realm: DynamicRealm) {
        
        realm.schema.get("RoomSummaryEntity")
                ?.addField(RoomSummaryEntityFields.HAS_FAILED_SENDING, Boolean::class.java)
                ?.transform { obj ->
                    obj.setBoolean(RoomSummaryEntityFields.HAS_FAILED_SENDING, false)
                }
    }
}
