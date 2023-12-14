

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.presence.UserPresenceEntity
import org.matrix.android.sdk.internal.database.model.presence.UserPresenceEntityFields

internal fun UserPresenceEntity.Companion.where(realm: Realm, userId: String): RealmQuery<UserPresenceEntity> {
    return realm
            .where<UserPresenceEntity>()
            .equalTo(UserPresenceEntityFields.USER_ID, userId)
}
