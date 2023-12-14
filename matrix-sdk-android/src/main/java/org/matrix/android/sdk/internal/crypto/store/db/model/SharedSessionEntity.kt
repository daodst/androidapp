

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmObject
import io.realm.annotations.Index


internal open class SharedSessionEntity(
        var roomId: String? = null,
        var algorithm: String? = null,
        @Index var sessionId: String? = null,
        @Index var userId: String? = null,
        @Index var deviceId: String? = null,
        @Index var deviceIdentityKey: String? = null,
        var chainIndex: Int? = null
) : RealmObject() {

    companion object
}
