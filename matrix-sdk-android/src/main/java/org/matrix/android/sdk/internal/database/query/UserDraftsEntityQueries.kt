

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.UserDraftsEntity
import org.matrix.android.sdk.internal.database.model.UserDraftsEntityFields

internal fun UserDraftsEntity.Companion.where(realm: Realm, roomId: String? = null): RealmQuery<UserDraftsEntity> {
    val query = realm.where<UserDraftsEntity>()
    if (roomId != null) {
        query.equalTo(UserDraftsEntityFields.ROOM_SUMMARY_ENTITY.ROOM_ID, roomId)
    }
    return query
}
