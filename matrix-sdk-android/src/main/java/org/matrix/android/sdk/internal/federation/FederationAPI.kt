

package org.matrix.android.sdk.internal.federation

import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.GET

internal interface FederationAPI {
    @GET(NetworkConstants.URI_FEDERATION_PATH + "version")
    suspend fun getVersion(): FederationGetVersionResult
}
