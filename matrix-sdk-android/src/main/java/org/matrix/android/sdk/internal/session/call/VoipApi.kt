

package org.matrix.android.sdk.internal.session.call

import org.matrix.android.sdk.api.session.call.TurnServerResponse
import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.GET

internal interface VoipApi {

    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "voip/turnServer")
    suspend fun getTurnServer(): TurnServerResponse
}
