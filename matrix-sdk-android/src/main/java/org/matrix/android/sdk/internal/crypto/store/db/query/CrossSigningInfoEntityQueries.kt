

package org.matrix.android.sdk.internal.crypto.store.db.query

import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.crypto.store.db.model.CrossSigningInfoEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.UserEntityFields

internal fun CrossSigningInfoEntity.Companion.getOrCreate(realm: Realm, userId: String): CrossSigningInfoEntity {
    return realm.where<CrossSigningInfoEntity>()
            .equalTo(UserEntityFields.USER_ID, userId)
            .findFirst()
            ?: realm.createObject(userId)
}

internal fun CrossSigningInfoEntity.Companion.get(realm: Realm, userId: String): CrossSigningInfoEntity? {
    return realm.where<CrossSigningInfoEntity>()
            .equalTo(UserEntityFields.USER_ID, userId)
            .findFirst()
}
