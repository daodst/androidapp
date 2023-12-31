

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntityFields
import org.matrix.android.sdk.internal.util.Normalizer
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo019(realm: DynamicRealm,
                                   private val normalizer: Normalizer) : RealmMigrator(realm, 19) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.get("RoomSummaryEntity")
                ?.addField(RoomSummaryEntityFields.NORMALIZED_DISPLAY_NAME, String::class.java)
                ?.transform {
                    it.getString(RoomSummaryEntityFields.DISPLAY_NAME)?.let { displayName ->
                        val normalised = normalizer.normalize(displayName)
                        it.set(RoomSummaryEntityFields.NORMALIZED_DISPLAY_NAME, normalised)
                    }
                }
    }
}
