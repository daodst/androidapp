

package org.matrix.android.sdk.internal.crypto.store.db.query

import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.crypto.store.db.model.DeviceInfoEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.DeviceInfoEntityFields
import org.matrix.android.sdk.internal.crypto.store.db.model.createPrimaryKey


internal fun DeviceInfoEntity.Companion.getOrCreate(realm: Realm, userId: String, deviceId: String): DeviceInfoEntity {
    val key = DeviceInfoEntity.createPrimaryKey(userId, deviceId)

    return realm.where<DeviceInfoEntity>()
            .equalTo(DeviceInfoEntityFields.PRIMARY_KEY, key)
            .findFirst()
            ?: realm.createObject<DeviceInfoEntity>(key)
                    .apply {
                        this.deviceId = deviceId
                    }
}
