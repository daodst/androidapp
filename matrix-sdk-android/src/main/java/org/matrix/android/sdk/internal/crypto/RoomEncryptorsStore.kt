

package org.matrix.android.sdk.internal.crypto

import org.matrix.android.sdk.internal.crypto.algorithms.IMXEncrypting
import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject

@SessionScope
internal class RoomEncryptorsStore @Inject constructor() {

    
    private val roomEncryptors = mutableMapOf<String, IMXEncrypting>()

    fun put(roomId: String, alg: IMXEncrypting) {
        synchronized(roomEncryptors) {
            roomEncryptors.put(roomId, alg)
        }
    }

    fun get(roomId: String): IMXEncrypting? {
        return synchronized(roomEncryptors) {
            roomEncryptors[roomId]
        }
    }
}
