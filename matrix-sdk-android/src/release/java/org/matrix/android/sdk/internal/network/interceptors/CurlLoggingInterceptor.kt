

package org.matrix.android.sdk.internal.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import org.matrix.android.sdk.internal.di.MatrixScope
import java.io.IOException
import javax.inject.Inject


@MatrixScope
internal class CurlLoggingInterceptor @Inject constructor() :
        Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}
