
package org.matrix.android.sdk.internal.session.room.relation.threads

import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.api.session.room.threads.model.ThreadSummaryUpdateType
import org.matrix.android.sdk.internal.crypto.DefaultCryptoService
import org.matrix.android.sdk.internal.database.helper.createOrUpdate
import org.matrix.android.sdk.internal.database.model.RoomEntity
import org.matrix.android.sdk.internal.database.model.threads.ThreadSummaryEntity
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.filter.FilterFactory
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.session.room.timeline.PaginationDirection
import org.matrix.android.sdk.internal.session.room.timeline.PaginationResponse
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.awaitTransaction
import timber.log.Timber
import javax.inject.Inject


internal interface FetchThreadSummariesTask : Task<FetchThreadSummariesTask.Params, DefaultFetchThreadSummariesTask.Result> {
    data class Params(
            val roomId: String,
            val from: String = "",
            val limit: Int = 500,
            val isUserParticipating: Boolean = true
    )
}

internal class DefaultFetchThreadSummariesTask @Inject constructor(
        private val roomAPI: RoomAPI,
        private val globalErrorReceiver: GlobalErrorReceiver,
        @SessionDatabase private val monarchy: Monarchy,
        private val cryptoService: DefaultCryptoService,
        @UserId private val userId: String,
) : FetchThreadSummariesTask {

    override suspend fun execute(params: FetchThreadSummariesTask.Params): Result {
        val filter = FilterFactory.createThreadsFilter(
                numberOfEvents = params.limit,
                userId = if (params.isUserParticipating) userId else null).toJSONString()

        val response = executeRequest(
                globalErrorReceiver,
                canRetry = true
        ) {
            roomAPI.getRoomMessagesFrom(params.roomId, params.from, PaginationDirection.BACKWARDS.value, params.limit, filter)
        }

        Timber.i("###THREADS DefaultFetchThreadSummariesTask Fetched size:${response.events.size} nextBatch:${response.end} ")

        return handleResponse(response, params)
    }

    private suspend fun handleResponse(response: PaginationResponse,
                                       params: FetchThreadSummariesTask.Params): Result {
        val rootThreadList = response.events
        monarchy.awaitTransaction { realm ->
            val roomEntity = RoomEntity.where(realm, roomId = params.roomId).findFirst() ?: return@awaitTransaction

            val roomMemberContentsByUser = HashMap<String, RoomMemberContent?>()
            for (rootThreadEvent in rootThreadList) {
                if (rootThreadEvent.eventId == null || rootThreadEvent.senderId == null || rootThreadEvent.type == null) {
                    continue
                }

                ThreadSummaryEntity.createOrUpdate(
                        threadSummaryType = ThreadSummaryUpdateType.REPLACE,
                        realm = realm,
                        roomId = params.roomId,
                        rootThreadEvent = rootThreadEvent,
                        roomMemberContentsByUser = roomMemberContentsByUser,
                        roomEntity = roomEntity,
                        userId = userId,
                        cryptoService = cryptoService)
            }
        }
        return Result.SUCCESS
    }

    enum class Result {
        SHOULD_FETCH_MORE,
        REACHED_END,
        SUCCESS
    }
}
