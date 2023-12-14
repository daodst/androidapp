

package org.matrix.android.sdk.internal.crypto.store.db.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.crypto.store.db.model.DeviceInfoEntityFields
import org.matrix.android.sdk.internal.crypto.store.db.model.SharedSessionEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateCryptoTo014(realm: DynamicRealm) : RealmMigrator(realm, 14) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.get("SharedSessionEntity")
                ?.addField(SharedSessionEntityFields.DEVICE_IDENTITY_KEY, String::class.java)
                ?.addIndex(SharedSessionEntityFields.DEVICE_IDENTITY_KEY)
                ?.transform {
                    val sharedUserId = it.getString(SharedSessionEntityFields.USER_ID)
                    val sharedDeviceId = it.getString(SharedSessionEntityFields.DEVICE_ID)
                    val knownDevice = realm.where("DeviceInfoEntity")
                            .equalTo(DeviceInfoEntityFields.USER_ID, sharedUserId)
                            .equalTo(DeviceInfoEntityFields.DEVICE_ID, sharedDeviceId)
                            .findFirst()
                    it.setString(SharedSessionEntityFields.DEVICE_IDENTITY_KEY, knownDevice?.getString(DeviceInfoEntityFields.IDENTITY_KEY))
                }
    }
}
