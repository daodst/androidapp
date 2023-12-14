

package org.matrix.android.sdk.internal.crypto.store.db.query

import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.crypto.store.db.model.CryptoRoomEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.CryptoRoomEntityFields


internal fun CryptoRoomEntity.Companion.getOrCreate(realm: Realm, roomId: String): CryptoRoomEntity {
    return getById(realm, roomId) ?: realm.createObject(roomId)
}


internal fun CryptoRoomEntity.Companion.getById(realm: Realm, roomId: String): CryptoRoomEntity? {
    return realm.where<CryptoRoomEntity>()
            .equalTo(CryptoRoomEntityFields.ROOM_ID, roomId)
            .findFirst()
}
