

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.RemarkEntity
import org.matrix.android.sdk.internal.database.model.RemarkEntityFields

internal fun RemarkEntity.Companion.where(realm: Realm,userId: String): RealmQuery<RemarkEntity> {
    val query = realm
            .where<RemarkEntity>()
            .equalTo(RemarkEntityFields.USER_ID, userId)
    return query
}
