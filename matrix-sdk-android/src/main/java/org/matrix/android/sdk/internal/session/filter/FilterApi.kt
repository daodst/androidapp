
package org.matrix.android.sdk.internal.session.filter

import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface FilterApi {

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "user/{userId}/filter")
    suspend fun uploadFilter(@Path("userId") userId: String,
                             @Body body: Filter): FilterResponse

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "user/{userId}/filter/{filterId}")
    suspend fun getFilterById(@Path("userId") userId: String,
                              @Path("filterId") filterId: String): Filter
}
