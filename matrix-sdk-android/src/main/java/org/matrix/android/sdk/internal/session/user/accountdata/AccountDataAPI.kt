

package org.matrix.android.sdk.internal.session.user.accountdata

import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface AccountDataAPI {

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "user/{userId}/account_data/{type}")
    suspend fun setAccountData(@Path("userId") userId: String,
                               @Path("type") type: String,
                               @Body params: Any)
}
