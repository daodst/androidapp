

package org.matrix.android.sdk.internal.session.account

import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AccountAPI {

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "account/password")
    suspend fun changePassword(@Body params: ChangePasswordParams)

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "account/deactivate")
    suspend fun deactivate(@Body params: DeactivateAccountParams)
}
