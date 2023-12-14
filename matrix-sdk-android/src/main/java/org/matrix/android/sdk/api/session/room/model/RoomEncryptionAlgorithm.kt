

package org.matrix.android.sdk.api.session.room.model

import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM

sealed class RoomEncryptionAlgorithm {

    abstract class SupportedAlgorithm(val alg: String) : RoomEncryptionAlgorithm()

    object Megolm : SupportedAlgorithm(MXCRYPTO_ALGORITHM_MEGOLM)

    data class UnsupportedAlgorithm(val name: String?) : RoomEncryptionAlgorithm()
}
