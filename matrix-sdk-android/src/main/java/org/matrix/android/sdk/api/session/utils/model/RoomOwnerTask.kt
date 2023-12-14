

package org.matrix.android.sdk.api.session.utils.model

import org.matrix.android.sdk.api.session.utils.UtilsAPI
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject


internal interface RoomOwnerTask : Task<RoomOwnerTask.Params, String> {

    data class Params(
            val roomId: String,
    )
}

internal class DefaultRoomOwnerTask @Inject constructor(
        private val utilsAPI: UtilsAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : RoomOwnerTask {

    override suspend fun execute(params: RoomOwnerTask.Params): String {
        return executeRequest(globalErrorReceiver) {
            utilsAPI.getRoomOwner(params.roomId)
        }
    }
}
