

package org.matrix.android.sdk.internal.session.identity

import org.matrix.android.sdk.api.session.openid.OpenIdToken
import org.matrix.android.sdk.internal.network.NetworkConstants
import org.matrix.android.sdk.internal.session.identity.model.IdentityRegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


internal interface IdentityAuthAPI {

    
    @GET(NetworkConstants.URI_IDENTITY_PREFIX_PATH)
    suspend fun ping()

    
    @GET("_matrix/identity/api/v1")
    suspend fun pingV1()

    
    @POST(NetworkConstants.URI_IDENTITY_PATH_V2 + "account/register")
    suspend fun register(@Body openIdToken: OpenIdToken): IdentityRegisterResponse
}
