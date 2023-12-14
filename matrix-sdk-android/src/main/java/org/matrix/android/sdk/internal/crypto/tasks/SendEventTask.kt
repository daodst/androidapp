
package org.matrix.android.sdk.internal.crypto.tasks

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.session.room.membership.LoadRoomMembersTask
import org.matrix.android.sdk.internal.session.room.send.LocalEchoRepository
import org.matrix.android.sdk.internal.task.Task
import timber.log.Timber
import javax.inject.Inject

internal interface SendEventTask : Task<SendEventTask.Params, String> {
    data class Params(
            val event: Event,
            val encrypt: Boolean
    )
}

internal class DefaultSendEventTask @Inject constructor(
        private val localEchoRepository: LocalEchoRepository,
        private val encryptEventTask: EncryptEventTask,
        private val loadRoomMembersTask: LoadRoomMembersTask,
        private val roomAPI: RoomAPI,
        private val globalErrorReceiver: GlobalErrorReceiver) : SendEventTask {

    override suspend fun execute(params: SendEventTask.Params): String {
        try {
            
            params.event.roomId
                    ?.takeIf { params.encrypt }
                    ?.let { roomId ->
                        loadRoomMembersTask.execute(LoadRoomMembersTask.Params(roomId))
                    }

            Timber.i("before params=${params.toString()}")
            val event = handleEncryption(params)
            Timber.i("after event=${event.toString()}")
            val localId = event.eventId!!
            localEchoRepository.updateSendState(localId, params.event.roomId, SendState.SENDING)
            val response = executeRequest(globalErrorReceiver) {
                roomAPI.send(
                        localId,
                        roomId = event.roomId ?: "",
                        content = event.content,
                        eventType = event.type ?: ""
                )
            }
            localEchoRepository.updateSendState(localId, params.event.roomId, SendState.SENT)
            return response.eventId.also {
                Timber.d("Event: $it just sent in ${params.event.roomId}")
            }
        } catch (e: Throwable) {
            throw e
        }
    }

    @Throws
    private suspend fun handleEncryption(params: SendEventTask.Params): Event {
        if (params.encrypt && !params.event.isEncrypted()) {
            return encryptEventTask.execute(EncryptEventTask.Params(
                    params.event.roomId ?: "",
                    params.event,
                    listOf("m.relates_to")
            ))
        }
        return params.event
    }
}
