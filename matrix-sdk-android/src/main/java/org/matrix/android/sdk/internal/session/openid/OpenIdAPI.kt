

package org.matrix.android.sdk.internal.session.openid

import org.matrix.android.sdk.api.session.openid.OpenIdToken
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

internal interface OpenIdAPI {

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "user/{userId}/openid/request_token")
    suspend fun openIdToken(@Path("userId") userId: String,
                            @Body body: JsonDict = emptyMap()): OpenIdToken
}
