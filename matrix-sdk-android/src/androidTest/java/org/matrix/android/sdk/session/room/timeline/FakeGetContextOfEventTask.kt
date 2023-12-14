

package org.matrix.android.sdk.session.room.timeline

import org.matrix.android.sdk.internal.session.room.timeline.GetContextOfEventTask
import org.matrix.android.sdk.internal.session.room.timeline.PaginationDirection
import org.matrix.android.sdk.internal.session.room.timeline.TokenChunkEventPersistor
import kotlin.random.Random

internal class FakeGetContextOfEventTask constructor(private val tokenChunkEventPersistor: TokenChunkEventPersistor) : GetContextOfEventTask {

    override suspend fun execute(params: GetContextOfEventTask.Params): TokenChunkEventPersistor.Result {
        val fakeEvents = RoomDataHelper.createFakeListOfEvents(30)
        val tokenChunkEvent = FakeTokenChunkEvent(
                Random.nextLong(System.currentTimeMillis()).toString(),
                Random.nextLong(System.currentTimeMillis()).toString(),
                fakeEvents
        )
        return tokenChunkEventPersistor.insertInDb(tokenChunkEvent, params.roomId, PaginationDirection.BACKWARDS)
    }
}
