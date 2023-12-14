

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.FilterEntity
import org.matrix.android.sdk.internal.session.filter.FilterFactory


internal fun FilterEntity.Companion.get(realm: Realm): FilterEntity? {
    return realm.where<FilterEntity>().findFirst()
}


internal fun FilterEntity.Companion.getOrCreate(realm: Realm): FilterEntity {
    return get(realm) ?: realm.createObject<FilterEntity>()
            .apply {
                filterBodyJson = FilterFactory.createDefaultFilter().toJSONString()
                roomEventFilterJson = FilterFactory.createDefaultRoomFilter().toJSONString()
                filterId = ""
            }
}
