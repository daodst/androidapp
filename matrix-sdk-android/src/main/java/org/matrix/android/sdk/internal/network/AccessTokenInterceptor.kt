

package org.matrix.android.sdk.internal.network

import okhttp3.Interceptor
import okhttp3.Response
import org.matrix.android.sdk.internal.network.token.AccessTokenProvider

internal class AccessTokenInterceptor(private val accessTokenProvider: AccessTokenProvider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        
        accessTokenProvider.getToken()?.let { token ->
            val newRequestBuilder = request.newBuilder()
            newRequestBuilder.header(HttpHeaders.Authorization, "Bearer $token")
            request = newRequestBuilder.build()
        }

        return chain.proceed(request)
    }
}
