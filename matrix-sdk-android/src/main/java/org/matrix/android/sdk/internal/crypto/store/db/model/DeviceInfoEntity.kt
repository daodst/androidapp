

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey

internal fun DeviceInfoEntity.Companion.createPrimaryKey(userId: String, deviceId: String) = "$userId|$deviceId"

internal open class DeviceInfoEntity(
        @PrimaryKey var primaryKey: String = "",
        var deviceId: String? = null,
        var identityKey: String? = null,
        var userId: String? = null,
        var isBlocked: Boolean? = null,
        var algorithmListJson: String? = null,
        var keysMapJson: String? = null,
        var signatureMapJson: String? = null,
        
        var unsignedMapJson: String? = null,
        var trustLevelEntity: TrustLevelEntity? = null,
        
        var firstTimeSeenLocalTs: Long? = null
) : RealmObject() {

    @LinkingObjects("devices")
    val users: RealmResults<UserEntity>? = null

    companion object
}

internal fun DeviceInfoEntity.deleteOnCascade() {
    trustLevelEntity?.deleteFromRealm()
    deleteFromRealm()
}
