

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.BreadcrumbsEntity

internal fun BreadcrumbsEntity.Companion.get(realm: Realm): BreadcrumbsEntity? {
    return realm.where<BreadcrumbsEntity>().findFirst()
}

internal fun BreadcrumbsEntity.Companion.getOrCreate(realm: Realm): BreadcrumbsEntity {
    return get(realm) ?: realm.createObject()
}
