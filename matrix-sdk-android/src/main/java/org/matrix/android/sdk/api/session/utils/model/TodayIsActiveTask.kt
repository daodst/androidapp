

package org.matrix.android.sdk.api.session.utils.model

import org.matrix.android.sdk.api.session.utils.UtilsAPI
import org.matrix.android.sdk.api.session.utils.bean.MyRoomList
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject


internal interface TodayIsActiveTask : Task<TodayIsActiveTask.Params?, Boolean> {

    data class Params(
            val account: String,
    )
}


internal class DefaultTodayIsActiveTask @Inject constructor(
        private val utilsAPI: UtilsAPI,
) : TodayIsActiveTask {
    override suspend fun execute(params: TodayIsActiveTask.Params?): Boolean {
        val response = executeRequest(null) {
            utilsAPI.isTodayActive()
        }
        return response
    }
}


internal interface Recent7DayActiveTask : Task<Recent7DayActiveTask.Params?, String> {

    data class Params(
            val account: String,
    )
}

internal class DefaultRecent7DayActiveTask @Inject constructor(
        private val utilsAPI: UtilsAPI,
) : Recent7DayActiveTask {
    override suspend fun execute(params: Recent7DayActiveTask.Params?): String {
        val response = executeRequest(null) {
            utilsAPI.recent7DayActive()
        }
        return response
    }
}


internal interface GetMyJoinedRoomsTask : Task<GetMyJoinedRoomsTask.Params?, MyRoomList?> {

    data class Params(
            val account: String,
    )
}
internal class DefaultGetMyJoinedRoomsTask @Inject constructor(
        private val utilsAPI: UtilsAPI,
) : GetMyJoinedRoomsTask {
    override suspend fun execute(params: GetMyJoinedRoomsTask.Params?): MyRoomList? {
        val response = executeRequest(null) {
            utilsAPI.getMyJoinedRooms()
        }
        return response
    }
}



internal interface AutoJoinRoomTask : Task<String, Any?>

internal class DefaultAutoJoinRoomTask @Inject constructor(
        private val utilsAPI: UtilsAPI,
) : AutoJoinRoomTask {
    override suspend fun execute(groupId: String): Any? {
        val response = executeRequest(null) {
            utilsAPI.httpAutoJoinRoom(groupId)
        }
        return response
    }
}

