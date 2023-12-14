

package org.matrix.android.sdk.internal.session.signout

import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.internal.auth.data.PasswordLoginParams
import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface SignOutAPI {

    
    @Headers("CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "login")
    suspend fun loginAgain(@Body loginParams: PasswordLoginParams): Credentials

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "logout")
    suspend fun signOut()
}
