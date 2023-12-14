

package org.matrix.android.sdk.internal.session.homeserver

import org.matrix.android.sdk.internal.auth.version.Versions
import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.GET

internal interface CapabilitiesAPI {
    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "capabilities")
    suspend fun getCapabilities(): GetCapabilitiesResult

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_ + "versions")
    suspend fun getVersions(): Versions

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_ + "versions")
    suspend fun ping()
}
