

package org.matrix.android.sdk.internal.crypto.algorithms.olm

import org.matrix.android.sdk.internal.crypto.MXOlmDevice
import org.matrix.android.sdk.internal.di.UserId
import javax.inject.Inject

internal class MXOlmDecryptionFactory @Inject constructor(private val olmDevice: MXOlmDevice,
                                                          @UserId private val userId: String) {

    fun create(): MXOlmDecryption {
        return MXOlmDecryption(
                olmDevice,
                userId)
    }
}
