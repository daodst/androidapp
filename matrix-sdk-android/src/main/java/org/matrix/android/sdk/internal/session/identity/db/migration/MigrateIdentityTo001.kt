

package org.matrix.android.sdk.internal.session.identity.db.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.session.identity.db.IdentityDataEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator
import timber.log.Timber

internal class MigrateIdentityTo001(realm: DynamicRealm) : RealmMigrator(realm, 1) {

    override fun doMigrate(realm: DynamicRealm) {
        Timber.d("Add field userConsent (Boolean) and set the value to false")
        realm.schema.get("IdentityDataEntity")
                ?.addField(IdentityDataEntityFields.USER_CONSENT, Boolean::class.java)
    }
}
