

package org.matrix.android.sdk.session.room.timeline

import org.matrix.android.sdk.internal.session.room.timeline.PaginationTask
import org.matrix.android.sdk.internal.session.room.timeline.TokenChunkEventPersistor
import javax.inject.Inject
import kotlin.random.Random

internal class FakePaginationTask @Inject constructor(private val tokenChunkEventPersistor: TokenChunkEventPersistor) : PaginationTask {

    override suspend fun execute(params: PaginationTask.Params): TokenChunkEventPersistor.Result {
        val fakeEvents = RoomDataHelper.createFakeListOfEvents(30)
        val tokenChunkEvent = FakeTokenChunkEvent(params.from, Random.nextLong(System.currentTimeMillis()).toString(), fakeEvents)
        return tokenChunkEventPersistor.insertInDb(tokenChunkEvent, params.roomId, params.direction)
    }
}
