

package org.matrix.android.sdk.internal.session.room.timeline

import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.crypto.model.OlmDecryptionResult
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.internal.crypto.EventDecryptor
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetEventTask : Task<GetEventTask.Params, Event> {
    data class Params(
            val roomId: String,
            val eventId: String
    )
}

internal class DefaultGetEventTask @Inject constructor(
        private val roomAPI: RoomAPI,
        private val globalErrorReceiver: GlobalErrorReceiver,
        private val eventDecryptor: EventDecryptor
) : GetEventTask {

    override suspend fun execute(params: GetEventTask.Params): Event {
        val event = executeRequest(globalErrorReceiver) {
            roomAPI.getEvent(params.roomId, params.eventId)
        }

        
        if (event.isEncrypted()) {
            tryOrNull(message = "Unable to decrypt the event") {
                eventDecryptor.decryptEvent(event, "")
            }
                    ?.let { result ->
                        event.mxDecryptionResult = OlmDecryptionResult(
                                payload = result.clearEvent,
                                senderKey = result.senderCurve25519Key,
                                keysClaimed = result.claimedEd25519Key?.let { mapOf("ed25519" to it) },
                                forwardingCurve25519KeyChain = result.forwardingCurve25519KeyChain
                        )
                    }
        }

        event.ageLocalTs = event.unsignedData?.age?.let { System.currentTimeMillis() - it }

        return event
    }
}
