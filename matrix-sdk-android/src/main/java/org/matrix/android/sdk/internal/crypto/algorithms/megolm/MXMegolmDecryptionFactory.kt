

package org.matrix.android.sdk.internal.crypto.algorithms.megolm

import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.internal.crypto.DeviceListManager
import org.matrix.android.sdk.internal.crypto.MXOlmDevice
import org.matrix.android.sdk.internal.crypto.OutgoingGossipingRequestManager
import org.matrix.android.sdk.internal.crypto.actions.EnsureOlmSessionsForDevicesAction
import org.matrix.android.sdk.internal.crypto.actions.MessageEncrypter
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.crypto.tasks.SendToDeviceTask
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.session.StreamEventsManager
import javax.inject.Inject

internal class MXMegolmDecryptionFactory @Inject constructor(
        @UserId private val userId: String,
        private val olmDevice: MXOlmDevice,
        private val deviceListManager: DeviceListManager,
        private val outgoingGossipingRequestManager: OutgoingGossipingRequestManager,
        private val messageEncrypter: MessageEncrypter,
        private val ensureOlmSessionsForDevicesAction: EnsureOlmSessionsForDevicesAction,
        private val cryptoStore: IMXCryptoStore,
        private val sendToDeviceTask: SendToDeviceTask,
        private val coroutineDispatchers: MatrixCoroutineDispatchers,
        private val cryptoCoroutineScope: CoroutineScope,
        private val eventsManager: Lazy<StreamEventsManager>
) {

    fun create(): MXMegolmDecryption {
        return MXMegolmDecryption(
                userId,
                olmDevice,
                deviceListManager,
                outgoingGossipingRequestManager,
                messageEncrypter,
                ensureOlmSessionsForDevicesAction,
                cryptoStore,
                sendToDeviceTask,
                coroutineDispatchers,
                cryptoCoroutineScope,
                eventsManager)
    }
}
