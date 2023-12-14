

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.ReadMarkerEntity
import org.matrix.android.sdk.internal.database.model.ReadMarkerEntityFields

internal fun ReadMarkerEntity.Companion.where(realm: Realm, roomId: String): RealmQuery<ReadMarkerEntity> {
    return realm.where<ReadMarkerEntity>()
            .equalTo(ReadMarkerEntityFields.ROOM_ID, roomId)
}

internal fun ReadMarkerEntity.Companion.getOrCreate(realm: Realm, roomId: String): ReadMarkerEntity {
    return where(realm, roomId).findFirst() ?: realm.createObject(roomId)
}
