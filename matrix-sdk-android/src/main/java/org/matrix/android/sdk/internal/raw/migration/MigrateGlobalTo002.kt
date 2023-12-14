

package org.matrix.android.sdk.internal.raw.migration

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import org.matrix.android.sdk.internal.database.model.RemarkEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateGlobalTo002(realm: DynamicRealm) : RealmMigrator(realm, 1) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.create("RemarkEntity").addField(RemarkEntityFields.USER_ID, String::class.java, FieldAttribute.PRIMARY_KEY)
                    .addField(RemarkEntityFields.REMARK, String::class.java, FieldAttribute.REQUIRED)
                    .addField(RemarkEntityFields.IS_SYNC, Int::class.java, FieldAttribute.REQUIRED)
    }
}
