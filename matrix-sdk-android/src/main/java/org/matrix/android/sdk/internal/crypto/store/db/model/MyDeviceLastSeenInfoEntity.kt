

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class MyDeviceLastSeenInfoEntity(
        
        @PrimaryKey var deviceId: String? = null,
        
        var displayName: String? = null,
        
        var lastSeenTs: Long? = null,
        
        var lastSeenIp: String? = null
) : RealmObject() {

    companion object
}
