

package org.matrix.android.sdk.internal.auth.db.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.auth.db.SessionParamsEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator
import timber.log.Timber

internal class MigrateAuthTo002(realm: DynamicRealm) : RealmMigrator(realm, 2) {

    override fun doMigrate(realm: DynamicRealm) {
        Timber.d("Add boolean isTokenValid in SessionParamsEntity, with value true")

        realm.schema.get("SessionParamsEntity")
                ?.addField(SessionParamsEntityFields.IS_TOKEN_VALID, Boolean::class.java)
                ?.transform { it.set(SessionParamsEntityFields.IS_TOKEN_VALID, true) }
    }
}
