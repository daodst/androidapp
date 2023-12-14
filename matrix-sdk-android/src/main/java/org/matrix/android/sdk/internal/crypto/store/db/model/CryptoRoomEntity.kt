

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class CryptoRoomEntity(
        @PrimaryKey var roomId: String? = null,
        var algorithm: String? = null,
        var shouldEncryptForInvitedMembers: Boolean? = null,
        var blacklistUnverifiedDevices: Boolean = false,
        
        
        
        var outboundSessionInfo: OutboundGroupSessionInfoEntity? = null,
        
        
        var wasEncryptedOnce: Boolean? = false
) :
        RealmObject() {

    companion object
}
