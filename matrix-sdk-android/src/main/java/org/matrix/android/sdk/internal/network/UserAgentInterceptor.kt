

package org.matrix.android.sdk.internal.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class UserAgentInterceptor @Inject constructor(private val userAgentHolder: UserAgentHolder) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val newRequestBuilder = request.newBuilder()
        
        userAgentHolder.userAgent
                .takeIf { it.isNotBlank() }
                ?.let {
                    newRequestBuilder.header(HttpHeaders.UserAgent, it)
                }
        request = newRequestBuilder.build()
        return chain.proceed(request)
    }
}
