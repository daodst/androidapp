

package org.matrix.android.sdk.internal.session.room

import org.matrix.android.sdk.api.session.room.AliasAvailabilityResult
import org.matrix.android.sdk.api.session.room.RoomDirectoryService
import org.matrix.android.sdk.api.session.room.alias.RoomAliasError
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoomsParams
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoomsResponse
import org.matrix.android.sdk.internal.session.room.alias.RoomAliasAvailabilityChecker
import org.matrix.android.sdk.internal.session.room.directory.GetPublicRoomTask
import org.matrix.android.sdk.internal.session.room.directory.GetRoomDirectoryVisibilityTask
import org.matrix.android.sdk.internal.session.room.directory.SetRoomDirectoryVisibilityTask
import javax.inject.Inject

internal class DefaultRoomDirectoryService @Inject constructor(
        private val getPublicRoomTask: GetPublicRoomTask,
        private val getRoomDirectoryVisibilityTask: GetRoomDirectoryVisibilityTask,
        private val setRoomDirectoryVisibilityTask: SetRoomDirectoryVisibilityTask,
        private val roomAliasAvailabilityChecker: RoomAliasAvailabilityChecker
) : RoomDirectoryService {

    override suspend fun getPublicRooms(server: String?,
                                        publicRoomsParams: PublicRoomsParams): PublicRoomsResponse {
        return getPublicRoomTask.execute(GetPublicRoomTask.Params(server, publicRoomsParams))
    }

    override suspend fun getRoomDirectoryVisibility(roomId: String): RoomDirectoryVisibility {
        return getRoomDirectoryVisibilityTask.execute(GetRoomDirectoryVisibilityTask.Params(roomId))
    }

    override suspend fun setRoomDirectoryVisibility(roomId: String, roomDirectoryVisibility: RoomDirectoryVisibility) {
        setRoomDirectoryVisibilityTask.execute(SetRoomDirectoryVisibilityTask.Params(roomId, roomDirectoryVisibility))
    }

    override suspend fun checkAliasAvailability(aliasLocalPart: String?): AliasAvailabilityResult {
        return try {
            roomAliasAvailabilityChecker.check(aliasLocalPart)
            AliasAvailabilityResult.Available
        } catch (failure: RoomAliasError) {
            AliasAvailabilityResult.NotAvailable(failure)
        }
    }
}
