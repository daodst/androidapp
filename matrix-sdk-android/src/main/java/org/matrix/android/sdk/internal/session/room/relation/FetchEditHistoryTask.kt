
package org.matrix.android.sdk.internal.session.room.relation

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.internal.crypto.CryptoSessionInfoProvider
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface FetchEditHistoryTask : Task<FetchEditHistoryTask.Params, List<Event>> {
    data class Params(
            val roomId: String,
            val eventId: String
    )
}

internal class DefaultFetchEditHistoryTask @Inject constructor(
        private val roomAPI: RoomAPI,
        private val globalErrorReceiver: GlobalErrorReceiver,
        private val cryptoSessionInfoProvider: CryptoSessionInfoProvider
) : FetchEditHistoryTask {

    override suspend fun execute(params: FetchEditHistoryTask.Params): List<Event> {
        val isRoomEncrypted = cryptoSessionInfoProvider.isRoomEncrypted(params.roomId)
        val response = executeRequest(globalErrorReceiver) {
            roomAPI.getRelations(
                    roomId = params.roomId,
                    eventId = params.eventId,
                    relationType = RelationType.REPLACE,
                    eventType = if (isRoomEncrypted) EventType.ENCRYPTED else EventType.MESSAGE
            )
        }

        
        val originalSenderId = response.originalEvent?.senderId
        val events = response.chunks
                .filter { it.senderId == originalSenderId }
                .filter { !it.isRedacted() }
        return events + listOfNotNull(response.originalEvent)
    }
}
