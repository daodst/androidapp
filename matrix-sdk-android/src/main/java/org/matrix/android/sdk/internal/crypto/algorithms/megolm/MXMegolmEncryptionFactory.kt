

package org.matrix.android.sdk.internal.crypto.algorithms.megolm

import kotlinx.coroutines.CoroutineScope
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.internal.crypto.DeviceListManager
import org.matrix.android.sdk.internal.crypto.MXOlmDevice
import org.matrix.android.sdk.internal.crypto.actions.EnsureOlmSessionsForDevicesAction
import org.matrix.android.sdk.internal.crypto.actions.MessageEncrypter
import org.matrix.android.sdk.internal.crypto.keysbackup.DefaultKeysBackupService
import org.matrix.android.sdk.internal.crypto.repository.WarnOnUnknownDeviceRepository
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.crypto.tasks.SendToDeviceTask
import org.matrix.android.sdk.internal.di.DeviceId
import org.matrix.android.sdk.internal.di.UserId
import javax.inject.Inject

internal class MXMegolmEncryptionFactory @Inject constructor(
        private val olmDevice: MXOlmDevice,
        private val defaultKeysBackupService: DefaultKeysBackupService,
        private val cryptoStore: IMXCryptoStore,
        private val deviceListManager: DeviceListManager,
        private val ensureOlmSessionsForDevicesAction: EnsureOlmSessionsForDevicesAction,
        @UserId private val userId: String,
        @DeviceId private val deviceId: String?,
        private val sendToDeviceTask: SendToDeviceTask,
        private val messageEncrypter: MessageEncrypter,
        private val warnOnUnknownDevicesRepository: WarnOnUnknownDeviceRepository,
        private val coroutineDispatchers: MatrixCoroutineDispatchers,
        private val cryptoCoroutineScope: CoroutineScope) {

    fun create(roomId: String): MXMegolmEncryption {
        return MXMegolmEncryption(
                roomId = roomId,
                olmDevice = olmDevice,
                defaultKeysBackupService = defaultKeysBackupService,
                cryptoStore = cryptoStore,
                deviceListManager = deviceListManager,
                ensureOlmSessionsForDevicesAction = ensureOlmSessionsForDevicesAction,
                myUserId = userId,
                myDeviceId = deviceId!!,
                sendToDeviceTask = sendToDeviceTask,
                messageEncrypter = messageEncrypter,
                warnOnUnknownDevicesRepository = warnOnUnknownDevicesRepository,
                coroutineDispatchers = coroutineDispatchers,
                cryptoCoroutineScope = cryptoCoroutineScope
        )
    }
}
