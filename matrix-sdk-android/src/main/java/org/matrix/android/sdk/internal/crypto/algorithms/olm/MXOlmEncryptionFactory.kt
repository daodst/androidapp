

package org.matrix.android.sdk.internal.crypto.algorithms.olm

import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.internal.crypto.DeviceListManager
import org.matrix.android.sdk.internal.crypto.MXOlmDevice
import org.matrix.android.sdk.internal.crypto.actions.EnsureOlmSessionsForUsersAction
import org.matrix.android.sdk.internal.crypto.actions.MessageEncrypter
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import javax.inject.Inject

internal class MXOlmEncryptionFactory @Inject constructor(private val olmDevice: MXOlmDevice,
                                                          private val cryptoStore: IMXCryptoStore,
                                                          private val messageEncrypter: MessageEncrypter,
                                                          private val deviceListManager: DeviceListManager,
                                                          private val coroutineDispatchers: MatrixCoroutineDispatchers,
                                                          private val ensureOlmSessionsForUsersAction: EnsureOlmSessionsForUsersAction) {

    fun create(roomId: String): MXOlmEncryption {
        return MXOlmEncryption(
                roomId,
                olmDevice,
                cryptoStore,
                messageEncrypter,
                deviceListManager,
                ensureOlmSessionsForUsersAction)
    }
}
