

package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.internal.crypto.tasks.SendToDeviceTask
import org.matrix.android.sdk.internal.di.DeviceId
import org.matrix.android.sdk.internal.task.TaskExecutor
import javax.inject.Inject

internal class VerificationTransportToDeviceFactory @Inject constructor(
        private val sendToDeviceTask: SendToDeviceTask,
        @DeviceId val myDeviceId: String?,
        private val taskExecutor: TaskExecutor) {

    fun createTransport(tx: DefaultVerificationTransaction?): VerificationTransportToDevice {
        return VerificationTransportToDevice(tx, sendToDeviceTask, myDeviceId, taskExecutor)
    }
}
