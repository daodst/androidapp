

package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.internal.di.DeviceId
import org.matrix.android.sdk.internal.di.SessionId
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.di.WorkManagerProvider
import org.matrix.android.sdk.internal.session.room.send.LocalEchoEventFactory
import org.matrix.android.sdk.internal.task.TaskExecutor
import javax.inject.Inject

internal class VerificationTransportRoomMessageFactory @Inject constructor(
        private val workManagerProvider: WorkManagerProvider,
        @SessionId
        private val sessionId: String,
        @UserId
        private val userId: String,
        @DeviceId
        private val deviceId: String?,
        private val localEchoEventFactory: LocalEchoEventFactory,
        private val taskExecutor: TaskExecutor
) {

    fun createTransport(roomId: String, tx: DefaultVerificationTransaction?): VerificationTransportRoomMessage {
        return VerificationTransportRoomMessage(workManagerProvider,
                sessionId,
                userId,
                deviceId,
                roomId,
                localEchoEventFactory,
                tx,
                taskExecutor.executorScope)
    }
}
