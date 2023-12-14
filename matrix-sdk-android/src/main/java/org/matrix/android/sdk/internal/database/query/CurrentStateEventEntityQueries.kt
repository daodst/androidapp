

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.createObject
import org.matrix.android.sdk.internal.database.model.CurrentStateEventEntity
import org.matrix.android.sdk.internal.database.model.CurrentStateEventEntityFields

internal fun CurrentStateEventEntity.Companion.whereType(realm: Realm,
                                                         roomId: String,
                                                         type: String): RealmQuery<CurrentStateEventEntity> {
    return realm.where(CurrentStateEventEntity::class.java)
            .equalTo(CurrentStateEventEntityFields.ROOM_ID, roomId)
            .equalTo(CurrentStateEventEntityFields.TYPE, type)
}

internal fun CurrentStateEventEntity.Companion.whereStateKey(realm: Realm,
                                                             roomId: String,
                                                             type: String,
                                                             stateKey: String): RealmQuery<CurrentStateEventEntity> {
    return whereType(realm = realm, roomId = roomId, type = type)
            .equalTo(CurrentStateEventEntityFields.STATE_KEY, stateKey)
}

internal fun CurrentStateEventEntity.Companion.getOrNull(realm: Realm,
                                                         roomId: String,
                                                         stateKey: String,
                                                         type: String): CurrentStateEventEntity? {
    return whereStateKey(realm = realm, roomId = roomId, type = type, stateKey = stateKey).findFirst()
}

internal fun CurrentStateEventEntity.Companion.getOrCreate(realm: Realm,
                                                           roomId: String,
                                                           stateKey: String,
                                                           type: String): CurrentStateEventEntity {
    return getOrNull(realm = realm, roomId = roomId, stateKey = stateKey, type = type) ?: create(realm, roomId, stateKey, type)
}

private fun create(realm: Realm,
                   roomId: String,
                   stateKey: String,
                   type: String): CurrentStateEventEntity {
    return realm.createObject<CurrentStateEventEntity>().apply {
        this.type = type
        this.roomId = roomId
        this.stateKey = stateKey
    }
}
