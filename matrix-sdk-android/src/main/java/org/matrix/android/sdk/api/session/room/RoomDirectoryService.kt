

package org.matrix.android.sdk.api.session.room

import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoomsParams
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoomsResponse


interface RoomDirectoryService {

    
    suspend fun getPublicRooms(server: String?,
                               publicRoomsParams: PublicRoomsParams): PublicRoomsResponse

    
    suspend fun getRoomDirectoryVisibility(roomId: String): RoomDirectoryVisibility

    
    suspend fun setRoomDirectoryVisibility(roomId: String, roomDirectoryVisibility: RoomDirectoryVisibility)

    suspend fun checkAliasAvailability(aliasLocalPart: String?): AliasAvailabilityResult
}
