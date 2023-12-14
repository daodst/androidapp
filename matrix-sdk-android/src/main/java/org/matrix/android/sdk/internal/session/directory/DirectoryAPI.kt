

package org.matrix.android.sdk.internal.session.directory

import org.matrix.android.sdk.api.session.room.alias.RoomAliasDescription
import org.matrix.android.sdk.internal.network.NetworkConstants
import org.matrix.android.sdk.internal.session.room.alias.AddRoomAliasBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface DirectoryAPI {
    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "directory/room/{roomAlias}")
    suspend fun getRoomIdByAlias(@Path("roomAlias") roomAlias: String): RoomAliasDescription

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "directory/list/room/{roomId}")
    suspend fun getRoomDirectoryVisibility(@Path("roomId") roomId: String): RoomDirectoryVisibilityJson

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "directory/list/room/{roomId}")
    suspend fun setRoomDirectoryVisibility(@Path("roomId") roomId: String,
                                           @Body body: RoomDirectoryVisibilityJson)

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "directory/room/{roomAlias}")
    suspend fun addRoomAlias(@Path("roomAlias") roomAlias: String,
                             @Body body: AddRoomAliasBody)

    
    @DELETE(NetworkConstants.URI_API_PREFIX_PATH_R0 + "directory/room/{roomAlias}")
    suspend fun deleteRoomAlias(@Path("roomAlias") roomAlias: String)
}
