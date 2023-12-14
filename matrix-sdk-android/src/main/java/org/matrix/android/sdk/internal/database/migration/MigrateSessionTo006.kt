

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.PreviewUrlCacheEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo006(realm: DynamicRealm) : RealmMigrator(realm, 6) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.create("PreviewUrlCacheEntity")
                .addField(PreviewUrlCacheEntityFields.URL, String::class.java)
                .setRequired(PreviewUrlCacheEntityFields.URL, true)
                .addPrimaryKey(PreviewUrlCacheEntityFields.URL)
                .addField(PreviewUrlCacheEntityFields.URL_FROM_SERVER, String::class.java)
                .addField(PreviewUrlCacheEntityFields.SITE_NAME, String::class.java)
                .addField(PreviewUrlCacheEntityFields.TITLE, String::class.java)
                .addField(PreviewUrlCacheEntityFields.DESCRIPTION, String::class.java)
                .addField(PreviewUrlCacheEntityFields.MXC_URL, String::class.java)
                .addField(PreviewUrlCacheEntityFields.LAST_UPDATED_TIMESTAMP, Long::class.java)
    }
}
