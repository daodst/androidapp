

package org.matrix.android.sdk.internal.session.room.read

import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface MarkAllRoomsReadTask : Task<MarkAllRoomsReadTask.Params, Unit> {
    data class Params(
            val roomIds: List<String>
    )
}

internal class DefaultMarkAllRoomsReadTask @Inject constructor(private val readMarkersTask: SetReadMarkersTask) : MarkAllRoomsReadTask {

    override suspend fun execute(params: MarkAllRoomsReadTask.Params) {
        params.roomIds.forEach { roomId ->
            readMarkersTask.execute(SetReadMarkersTask.Params(roomId, forceReadMarker = true, forceReadReceipt = true))
        }
    }
}
