

package org.matrix.android.sdk.internal.session.profile

import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.auth.registration.SuccessResult
import org.matrix.android.sdk.internal.auth.registration.ValidationCodeBody
import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.*

internal interface ProfileAPI {
    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: String): JsonDict

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "account/3pid")
    suspend fun getThreePIDs(): AccountThreePidsResponse

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "profile/{userId}/displayname")
    suspend fun setDisplayName(@Path("userId") userId: String,
                               @Body body: SetDisplayNameBody)

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "profile/{userId}/avatar_url")
    suspend fun setAvatarUrl(@Path("userId") userId: String,
                             @Body body: SetAvatarUrlBody)

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "account/3pid/bind")
    suspend fun bindThreePid(@Body body: BindThreePidBody)

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_UNSTABLE + "account/3pid/unbind")
    suspend fun unbindThreePid(@Body body: UnbindThreePidBody): UnbindThreePidResponse

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "account/3pid/email/requestToken")
    suspend fun addEmail(@Body body: AddEmailBody): AddEmailResponse

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "account/3pid/msisdn/requestToken")
    suspend fun addMsisdn(@Body body: AddMsisdnBody): AddMsisdnResponse

    
    @POST
    suspend fun validateMsisdn(@Url url: String,
                               @Body params: ValidationCodeBody): SuccessResult

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "account/3pid/add")
    suspend fun finalizeAddThreePid(@Body body: FinalizeAddThreePidBody)

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "account/3pid/delete")
    suspend fun deleteThreePid(@Body body: DeleteThreePidBody): DeleteThreePidResponse

    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "server/pubKey")
    suspend fun getServerPublicKey(): String
}
