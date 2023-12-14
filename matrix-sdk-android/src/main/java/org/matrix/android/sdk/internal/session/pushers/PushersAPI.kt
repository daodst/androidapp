
package org.matrix.android.sdk.internal.session.pushers

import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface PushersAPI {

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "pushers")
    suspend fun getPushers(): GetPushersResponse

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "pushers/set")
    suspend fun setPusher(@Body jsonPusher: JsonPusher)
}
