

package org.matrix.android.sdk.internal.session.user

import org.matrix.android.sdk.internal.network.NetworkConstants
import org.matrix.android.sdk.internal.session.user.model.SearchUsersParams
import org.matrix.android.sdk.internal.session.user.model.SearchUsersResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface SearchUserAPI {

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "user_directory/search")
    suspend fun searchUsers(@Body searchUsersParams: SearchUsersParams): SearchUsersResponse
}
