

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.PreviewUrlCacheEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo024(realm: DynamicRealm) : RealmMigrator(realm, 24) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.get("PreviewUrlCacheEntity")
                ?.addField(PreviewUrlCacheEntityFields.IMAGE_WIDTH, Int::class.java)
                ?.setNullable(PreviewUrlCacheEntityFields.IMAGE_WIDTH, true)
                ?.addField(PreviewUrlCacheEntityFields.IMAGE_HEIGHT, Int::class.java)
                ?.setNullable(PreviewUrlCacheEntityFields.IMAGE_HEIGHT, true)
    }
}
