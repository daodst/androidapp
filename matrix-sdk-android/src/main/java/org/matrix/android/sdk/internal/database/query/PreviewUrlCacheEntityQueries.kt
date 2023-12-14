

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.PreviewUrlCacheEntity
import org.matrix.android.sdk.internal.database.model.PreviewUrlCacheEntityFields


internal fun PreviewUrlCacheEntity.Companion.get(realm: Realm, url: String): PreviewUrlCacheEntity? {
    return realm.where<PreviewUrlCacheEntity>()
            .equalTo(PreviewUrlCacheEntityFields.URL, url)
            .findFirst()
}


internal fun PreviewUrlCacheEntity.Companion.getOrCreate(realm: Realm, url: String): PreviewUrlCacheEntity {
    return get(realm, url) ?: realm.createObject(url)
}
