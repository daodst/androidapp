
package org.matrix.android.sdk.internal.crypto.api

import org.matrix.android.sdk.api.session.crypto.model.DeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.DevicesListResponse
import org.matrix.android.sdk.internal.crypto.model.rest.DeleteDeviceParams
import org.matrix.android.sdk.internal.crypto.model.rest.KeyChangesResponse
import org.matrix.android.sdk.internal.crypto.model.rest.KeysClaimBody
import org.matrix.android.sdk.internal.crypto.model.rest.KeysClaimResponse
import org.matrix.android.sdk.internal.crypto.model.rest.KeysQueryBody
import org.matrix.android.sdk.internal.crypto.model.rest.KeysQueryResponse
import org.matrix.android.sdk.internal.crypto.model.rest.KeysUploadBody
import org.matrix.android.sdk.internal.crypto.model.rest.KeysUploadResponse
import org.matrix.android.sdk.internal.crypto.model.rest.SendToDeviceBody
import org.matrix.android.sdk.internal.crypto.model.rest.SignatureUploadResponse
import org.matrix.android.sdk.internal.crypto.model.rest.UpdateDeviceInfoBody
import org.matrix.android.sdk.internal.crypto.model.rest.UploadSigningKeysBody
import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

internal interface CryptoApi {

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "devices")
    suspend fun getDevices(): DevicesListResponse

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "devices/{deviceId}")
    suspend fun getDeviceInfo(@Path("deviceId") deviceId: String): DeviceInfo

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "keys/upload")
    suspend fun uploadKeys(@Body body: KeysUploadBody): KeysUploadResponse

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "keys/query")
    suspend fun downloadKeysForUsers(@Body params: KeysQueryBody): KeysQueryResponse

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "keys/device_signing/upload")
    suspend fun uploadSigningKeys(@Body params: UploadSigningKeysBody): KeysQueryResponse

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "keys/signatures/upload")
    suspend fun uploadSignatures(@Body params: Map<String, @JvmSuppressWildcards Any>?): SignatureUploadResponse

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "keys/claim")
    suspend fun claimOneTimeKeysForUsersDevices(@Body body: KeysClaimBody): KeysClaimResponse

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "sendToDevice/{eventType}/{txnId}")
    suspend fun sendToDevice(@Path("eventType") eventType: String,
                             @Path("txnId") transactionId: String,
                             @Body body: SendToDeviceBody)

    
    @HTTP(path = NetworkConstants.URI_API_PREFIX_PATH_R0 + "devices/{device_id}", method = "DELETE", hasBody = true)
    suspend fun deleteDevice(@Path("device_id") deviceId: String,
                             @Body params: DeleteDeviceParams)

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "devices/{device_id}")
    suspend fun updateDeviceInfo(@Path("device_id") deviceId: String,
                                 @Body params: UpdateDeviceInfoBody)

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "keys/changes")
    suspend fun getKeyChanges(@Query("from") oldToken: String,
                              @Query("to") newToken: String): KeyChangesResponse
}
