

package org.matrix.android.sdk.internal.crypto.store.db.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.crypto.store.db.model.SharedSessionEntityFields
import org.matrix.android.sdk.internal.crypto.store.db.model.WithHeldSessionEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateCryptoTo010(realm: DynamicRealm) : RealmMigrator(realm, 10) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.create("WithHeldSessionEntity")
                .addField(WithHeldSessionEntityFields.ROOM_ID, String::class.java)
                .addField(WithHeldSessionEntityFields.ALGORITHM, String::class.java)
                .addField(WithHeldSessionEntityFields.SESSION_ID, String::class.java)
                .addIndex(WithHeldSessionEntityFields.SESSION_ID)
                .addField(WithHeldSessionEntityFields.SENDER_KEY, String::class.java)
                .addIndex(WithHeldSessionEntityFields.SENDER_KEY)
                .addField(WithHeldSessionEntityFields.CODE_STRING, String::class.java)
                .addField(WithHeldSessionEntityFields.REASON, String::class.java)

        realm.schema.create("SharedSessionEntity")
                .addField(SharedSessionEntityFields.ROOM_ID, String::class.java)
                .addField(SharedSessionEntityFields.ALGORITHM, String::class.java)
                .addField(SharedSessionEntityFields.SESSION_ID, String::class.java)
                .addIndex(SharedSessionEntityFields.SESSION_ID)
                .addField(SharedSessionEntityFields.USER_ID, String::class.java)
                .addIndex(SharedSessionEntityFields.USER_ID)
                .addField(SharedSessionEntityFields.DEVICE_ID, String::class.java)
                .addIndex(SharedSessionEntityFields.DEVICE_ID)
                .addField(SharedSessionEntityFields.CHAIN_INDEX, Long::class.java)
                .setNullable(SharedSessionEntityFields.CHAIN_INDEX, true)
    }
}
