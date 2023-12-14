

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.HomeServerCapabilitiesEntity


internal fun HomeServerCapabilitiesEntity.Companion.get(realm: Realm): HomeServerCapabilitiesEntity? {
    return realm.where<HomeServerCapabilitiesEntity>().findFirst()
}


internal fun HomeServerCapabilitiesEntity.Companion.getOrCreate(realm: Realm): HomeServerCapabilitiesEntity {
    return get(realm) ?: realm.createObject()
}
