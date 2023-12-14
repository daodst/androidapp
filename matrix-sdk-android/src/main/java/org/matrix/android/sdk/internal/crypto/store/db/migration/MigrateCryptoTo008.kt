

package org.matrix.android.sdk.internal.crypto.store.db.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.internal.crypto.store.db.model.DeviceInfoEntityFields
import org.matrix.android.sdk.internal.crypto.store.db.model.MyDeviceLastSeenInfoEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateCryptoTo008(realm: DynamicRealm) : RealmMigrator(realm, 8) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.create("MyDeviceLastSeenInfoEntity")
                .addField(MyDeviceLastSeenInfoEntityFields.DEVICE_ID, String::class.java)
                .addPrimaryKey(MyDeviceLastSeenInfoEntityFields.DEVICE_ID)
                .addField(MyDeviceLastSeenInfoEntityFields.DISPLAY_NAME, String::class.java)
                .addField(MyDeviceLastSeenInfoEntityFields.LAST_SEEN_IP, String::class.java)
                .addField(MyDeviceLastSeenInfoEntityFields.LAST_SEEN_TS, Long::class.java)
                .setNullable(MyDeviceLastSeenInfoEntityFields.LAST_SEEN_TS, true)

        val now = System.currentTimeMillis()
        realm.schema.get("DeviceInfoEntity")
                ?.addField(DeviceInfoEntityFields.FIRST_TIME_SEEN_LOCAL_TS, Long::class.java)
                ?.setNullable(DeviceInfoEntityFields.FIRST_TIME_SEEN_LOCAL_TS, true)
                ?.transform { deviceInfoEntity ->
                    tryOrNull {
                        deviceInfoEntity.setLong(DeviceInfoEntityFields.FIRST_TIME_SEEN_LOCAL_TS, now)
                    }
                }
    }
}
