

package org.matrix.android.sdk.internal.session.identity

import org.matrix.android.sdk.api.session.identity.model.SignInvitationResult
import org.matrix.android.sdk.internal.auth.registration.SuccessResult
import org.matrix.android.sdk.internal.network.NetworkConstants
import org.matrix.android.sdk.internal.session.identity.model.IdentityAccountResponse
import org.matrix.android.sdk.internal.session.identity.model.IdentityHashDetailResponse
import org.matrix.android.sdk.internal.session.identity.model.IdentityLookUpParams
import org.matrix.android.sdk.internal.session.identity.model.IdentityLookUpResponse
import org.matrix.android.sdk.internal.session.identity.model.IdentityRequestOwnershipParams
import org.matrix.android.sdk.internal.session.identity.model.IdentityRequestTokenForEmailBody
import org.matrix.android.sdk.internal.session.identity.model.IdentityRequestTokenForMsisdnBody
import org.matrix.android.sdk.internal.session.identity.model.IdentityRequestTokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


internal interface IdentityAPI {
    
    @GET(NetworkConstants.URI_IDENTITY_PATH_V2 + "account")
    suspend fun getAccount(): IdentityAccountResponse

    
    @POST(NetworkConstants.URI_IDENTITY_PATH_V2 + "account/logout")
    suspend fun logout()

    
    @GET(NetworkConstants.URI_IDENTITY_PATH_V2 + "hash_details")
    suspend fun hashDetails(): IdentityHashDetailResponse

    
    @POST(NetworkConstants.URI_IDENTITY_PATH_V2 + "lookup")
    suspend fun lookup(@Body body: IdentityLookUpParams): IdentityLookUpResponse

    
    @POST(NetworkConstants.URI_IDENTITY_PATH_V2 + "validate/email/requestToken")
    suspend fun requestTokenToBindEmail(@Body body: IdentityRequestTokenForEmailBody): IdentityRequestTokenResponse

    
    @POST(NetworkConstants.URI_IDENTITY_PATH_V2 + "validate/msisdn/requestToken")
    suspend fun requestTokenToBindMsisdn(@Body body: IdentityRequestTokenForMsisdnBody): IdentityRequestTokenResponse

    
    @POST(NetworkConstants.URI_IDENTITY_PATH_V2 + "validate/{medium}/submitToken")
    suspend fun submitToken(@Path("medium") medium: String,
                            @Body body: IdentityRequestOwnershipParams): SuccessResult

    
    @POST(NetworkConstants.URI_IDENTITY_PATH_V1 + "sign-ed25519")
    suspend fun signInvitationDetails(
            @Query("token") token: String,
            @Query("private_key") privateKey: String,
            @Query("mxid") mxid: String
    ): SignInvitationResult
}
