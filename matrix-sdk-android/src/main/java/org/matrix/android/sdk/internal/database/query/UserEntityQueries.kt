

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.UserEntity
import org.matrix.android.sdk.internal.database.model.UserEntityFields

internal fun UserEntity.Companion.where(realm: Realm, userId: String): RealmQuery<UserEntity> {
    return realm
            .where<UserEntity>()
            .equalTo(UserEntityFields.USER_ID, userId)
}

internal fun UserEntity.Companion.contains(realm: Realm, userId: String): RealmQuery<UserEntity> {
    return realm
            .where<UserEntity>()
            .contains(UserEntityFields.USER_ID, userId)
}
