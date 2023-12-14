

package org.matrix.android.sdk.api.session.room.crypto

import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM

interface RoomCryptoService {

    fun isEncrypted(): Boolean

    fun encryptionAlgorithm(): String?

    fun shouldEncryptForInvitedMembers(): Boolean

    
    suspend fun enableEncryption(algorithm: String = MXCRYPTO_ALGORITHM_MEGOLM, force: Boolean = false)

    
    suspend fun prepareToEncrypt()
}
