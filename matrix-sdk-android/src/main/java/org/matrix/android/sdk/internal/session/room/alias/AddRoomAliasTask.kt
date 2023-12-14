

package org.matrix.android.sdk.internal.session.room.alias

import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.directory.DirectoryAPI
import org.matrix.android.sdk.internal.session.room.alias.RoomAliasAvailabilityChecker.Companion.toFullLocalAlias
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface AddRoomAliasTask : Task<AddRoomAliasTask.Params, Unit> {
    data class Params(
            val roomId: String,
            
            val aliasLocalPart: String
    )
}

internal class DefaultAddRoomAliasTask @Inject constructor(
        @UserId private val userId: String,
        private val directoryAPI: DirectoryAPI,
        private val aliasAvailabilityChecker: RoomAliasAvailabilityChecker,
        private val globalErrorReceiver: GlobalErrorReceiver
) : AddRoomAliasTask {

    override suspend fun execute(params: AddRoomAliasTask.Params) {
        aliasAvailabilityChecker.check(params.aliasLocalPart)

        executeRequest(globalErrorReceiver) {
            directoryAPI.addRoomAlias(
                    roomAlias = params.aliasLocalPart.toFullLocalAlias(userId),
                    body = AddRoomAliasBody(
                            roomId = params.roomId
                    )
            )
        }
    }
}
