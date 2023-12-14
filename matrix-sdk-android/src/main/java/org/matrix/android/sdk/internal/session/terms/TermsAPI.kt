

package org.matrix.android.sdk.internal.session.terms

import org.matrix.android.sdk.api.session.terms.TermsResponse
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.api.util.emptyJsonDict
import org.matrix.android.sdk.internal.network.HttpHeaders
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

internal interface TermsAPI {
    
    @GET
    suspend fun getTerms(@Url url: String): TermsResponse

    
    @POST
    suspend fun agreeToTerms(@Url url: String,
                             @Body params: AcceptTermsBody,
                             @Header(HttpHeaders.Authorization) token: String)

    
    @POST
    suspend fun register(@Url url: String,
                         @Body body: JsonDict = emptyJsonDict)
}
