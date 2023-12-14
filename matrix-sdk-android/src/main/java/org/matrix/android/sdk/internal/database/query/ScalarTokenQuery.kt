

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.ScalarTokenEntity
import org.matrix.android.sdk.internal.database.model.ScalarTokenEntityFields

internal fun ScalarTokenEntity.Companion.where(realm: Realm, serverUrl: String): RealmQuery<ScalarTokenEntity> {
    return realm
            .where<ScalarTokenEntity>()
            .equalTo(ScalarTokenEntityFields.SERVER_URL, serverUrl)
}
