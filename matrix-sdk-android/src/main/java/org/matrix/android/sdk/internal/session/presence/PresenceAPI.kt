

package org.matrix.android.sdk.internal.session.presence

import org.matrix.android.sdk.internal.network.NetworkConstants
import org.matrix.android.sdk.internal.session.presence.model.GetPresenceResponse
import org.matrix.android.sdk.internal.session.presence.model.SetPresenceBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface PresenceAPI {

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "presence/{userId}/status")
    suspend fun setPresence(@Path("userId") userId: String,
                            @Body body: SetPresenceBody)

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "presence/{userId}/status")
    suspend fun getPresence(@Path("userId") userId: String): GetPresenceResponse
}
