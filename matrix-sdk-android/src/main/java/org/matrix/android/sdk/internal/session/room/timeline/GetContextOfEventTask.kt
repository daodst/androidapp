

package org.matrix.android.sdk.internal.session.room.timeline

import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.filter.FilterRepository
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface GetContextOfEventTask : Task<GetContextOfEventTask.Params, TokenChunkEventPersistor.Result> {

    data class Params(
            val roomId: String,
            val eventId: String
    )
}

internal class DefaultGetContextOfEventTask @Inject constructor(
        private val roomAPI: RoomAPI,
        private val filterRepository: FilterRepository,
        private val tokenChunkEventPersistor: TokenChunkEventPersistor,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetContextOfEventTask {

    override suspend fun execute(params: GetContextOfEventTask.Params): TokenChunkEventPersistor.Result {
        val filter = filterRepository.getRoomFilter()
        val response = executeRequest(globalErrorReceiver) {
            
            roomAPI.getContextOfEvent(params.roomId, params.eventId, 0, filter)
        }
        return tokenChunkEventPersistor.insertInDb(response, params.roomId, PaginationDirection.FORWARDS)
    }
}
