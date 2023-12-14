

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.PendingThreePidEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo004(realm: DynamicRealm) : RealmMigrator(realm, 4) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.create("PendingThreePidEntity")
                .addField(PendingThreePidEntityFields.CLIENT_SECRET, String::class.java)
                .setRequired(PendingThreePidEntityFields.CLIENT_SECRET, true)
                .addField(PendingThreePidEntityFields.EMAIL, String::class.java)
                .addField(PendingThreePidEntityFields.MSISDN, String::class.java)
                .addField(PendingThreePidEntityFields.SEND_ATTEMPT, Int::class.java)
                .addField(PendingThreePidEntityFields.SID, String::class.java)
                .setRequired(PendingThreePidEntityFields.SID, true)
                .addField(PendingThreePidEntityFields.SUBMIT_URL, String::class.java)
    }
}
