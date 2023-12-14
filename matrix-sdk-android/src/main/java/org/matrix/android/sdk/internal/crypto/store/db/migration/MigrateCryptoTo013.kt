

package org.matrix.android.sdk.internal.crypto.store.db.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.util.database.RealmMigrator
import timber.log.Timber

internal class MigrateCryptoTo013(realm: DynamicRealm) : RealmMigrator(realm, 13) {

    override fun doMigrate(realm: DynamicRealm) {
        
        val trustLevelEntitySchema = realm.schema.get("TrustLevelEntity")

        
        var mainCounter = 0
        var deviceInfoCounter = 0
        var keyInfoCounter = 0
        val deleteCounter: Int

        trustLevelEntitySchema
                ?.addField("isLinked", Boolean::class.java)
                ?.transform { obj ->
                    
                    obj.set("isLinked", false)
                    mainCounter++
                }

        realm.schema.get("DeviceInfoEntity")?.transform { obj ->
            
            deviceInfoCounter++
            obj.getObject("trustLevelEntity")?.set("isLinked", true)
        }

        realm.schema.get("KeyInfoEntity")?.transform { obj ->
            
            keyInfoCounter++
            obj.getObject("trustLevelEntity")?.set("isLinked", true)
        }

        
        realm.where("TrustLevelEntity")
                .equalTo("isLinked", false)
                .findAll()
                .also { deleteCounter = it.size }
                .deleteAllFromRealm()

        trustLevelEntitySchema?.removeField("isLinked")

        Timber.w("TrustLevelEntity cleanup: $mainCounter entities")
        Timber.w("TrustLevelEntity cleanup: $deviceInfoCounter entities referenced in DeviceInfoEntities")
        Timber.w("TrustLevelEntity cleanup: $keyInfoCounter entities referenced in KeyInfoEntity")
        Timber.w("TrustLevelEntity cleanup: $deleteCounter entities deleted!")
        if (mainCounter != deviceInfoCounter + keyInfoCounter + deleteCounter) {
            Timber.e("TrustLevelEntity cleanup: Something is not correct...")
        }
    }
}
