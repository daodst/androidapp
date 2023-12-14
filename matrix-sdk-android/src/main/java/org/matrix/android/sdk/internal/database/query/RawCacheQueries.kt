

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.RawCacheEntity
import org.matrix.android.sdk.internal.database.model.RawCacheEntityFields


internal fun RawCacheEntity.Companion.get(realm: Realm, url: String): RawCacheEntity? {
    return realm.where<RawCacheEntity>()
            .equalTo(RawCacheEntityFields.URL, url)
            .findFirst()
}


internal fun RawCacheEntity.Companion.getOrCreate(realm: Realm, url: String): RawCacheEntity {
    return get(realm, url) ?: realm.createObject(url)
}
