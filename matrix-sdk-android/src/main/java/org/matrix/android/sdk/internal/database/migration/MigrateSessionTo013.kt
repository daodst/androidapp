

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.SpaceChildSummaryEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo013(realm: DynamicRealm) : RealmMigrator(realm, 13) {

    override fun doMigrate(realm: DynamicRealm) {
        
        realm.schema.get("SpaceChildSummaryEntity")
                ?.takeIf { !it.hasField(SpaceChildSummaryEntityFields.SUGGESTED) }
                ?.addField(SpaceChildSummaryEntityFields.SUGGESTED, Boolean::class.java)
                ?.setNullable(SpaceChildSummaryEntityFields.SUGGESTED, true)
    }
}
