

package org.matrix.android.sdk.internal.crypto.store.db.query

import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.crypto.store.db.model.UserEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.UserEntityFields
import org.matrix.android.sdk.internal.crypto.store.db.model.deleteOnCascade


internal fun UserEntity.Companion.getOrCreate(realm: Realm, userId: String): UserEntity {
    return realm.where<UserEntity>()
            .equalTo(UserEntityFields.USER_ID, userId)
            .findFirst()
            ?: realm.createObject(userId)
}


internal fun UserEntity.Companion.delete(realm: Realm, userId: String) {
    realm.where<UserEntity>()
            .equalTo(UserEntityFields.USER_ID, userId)
            .findFirst()
            ?.deleteOnCascade()
}
