

package org.matrix.android.sdk.internal.session.search

import org.matrix.android.sdk.internal.network.NetworkConstants
import org.matrix.android.sdk.internal.session.search.request.SearchRequestBody
import org.matrix.android.sdk.internal.session.search.response.SearchResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

internal interface SearchAPI {

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "search")
    suspend fun search(@Query("next_batch") nextBatch: String?,
                       @Body body: SearchRequestBody): SearchResponse
}
