
package org.matrix.android.sdk.internal.session.pushers.gateway

import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.Body
import retrofit2.http.POST

internal interface PushGatewayAPI {
    
    @POST(NetworkConstants.URI_PUSH_GATEWAY_PREFIX_PATH + "notify")
    suspend fun notify(@Body body: PushGatewayNotifyBody): PushGatewayNotifyResponse
}
