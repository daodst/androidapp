

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.UserEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator


internal class MigrateSessionTo033(realm: DynamicRealm) : RealmMigrator(realm, 32) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.get("UserEntity")
                ?.addRealmListField(UserEntityFields.TEL_NUMBERS.`$`, String::class.java)
    }
}
