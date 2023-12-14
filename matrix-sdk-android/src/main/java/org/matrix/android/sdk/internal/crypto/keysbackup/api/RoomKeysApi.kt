

package org.matrix.android.sdk.internal.crypto.keysbackup.api

import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysVersion
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysVersionResult
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.BackupKeysResult
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.CreateKeysBackupVersionBody
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.KeyBackupData
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.KeysBackupData
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.RoomKeysBackupData
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.UpdateKeysBackupVersionBody
import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


internal interface RoomKeysApi {

    

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/version")
    suspend fun createKeysBackupVersion(@Body createKeysBackupVersionBody: CreateKeysBackupVersionBody): KeysVersion

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/version")
    suspend fun getKeysBackupLastVersion(): KeysVersionResult

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/version/{version}")
    suspend fun getKeysBackupVersion(@Path("version") version: String): KeysVersionResult

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/version/{version}")
    suspend fun updateKeysBackupVersion(@Path("version") version: String,
                                        @Body keysBackupVersionBody: UpdateKeysBackupVersionBody)

    

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/keys/{roomId}/{sessionId}")
    suspend fun storeRoomSessionData(@Path("roomId") roomId: String,
                                     @Path("sessionId") sessionId: String,
                                     @Query("version") version: String,
                                     @Body keyBackupData: KeyBackupData): BackupKeysResult

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/keys/{roomId}")
    suspend fun storeRoomSessionsData(@Path("roomId") roomId: String,
                                      @Query("version") version: String,
                                      @Body roomKeysBackupData: RoomKeysBackupData): BackupKeysResult

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/keys")
    suspend fun storeSessionsData(@Query("version") version: String,
                                  @Body keysBackupData: KeysBackupData): BackupKeysResult

    

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/keys/{roomId}/{sessionId}")
    suspend fun getRoomSessionData(@Path("roomId") roomId: String,
                                   @Path("sessionId") sessionId: String,
                                   @Query("version") version: String): KeyBackupData

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/keys/{roomId}")
    suspend fun getRoomSessionsData(@Path("roomId") roomId: String,
                                    @Query("version") version: String): RoomKeysBackupData

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/keys")
    suspend fun getSessionsData(@Query("version") version: String): KeysBackupData

    

    
    @DELETE(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/keys/{roomId}/{sessionId}")
    suspend fun deleteRoomSessionData(@Path("roomId") roomId: String,
                                      @Path("sessionId") sessionId: String,
                                      @Query("version") version: String)

    
    @DELETE(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/keys/{roomId}")
    suspend fun deleteRoomSessionsData(@Path("roomId") roomId: String,
                                       @Query("version") version: String)

    
    @DELETE(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/keys")
    suspend fun deleteSessionsData(@Query("version") version: String)

    

    
    @DELETE(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "room_keys/version/{version}")
    suspend fun deleteBackup(@Path("version") version: String)
}
