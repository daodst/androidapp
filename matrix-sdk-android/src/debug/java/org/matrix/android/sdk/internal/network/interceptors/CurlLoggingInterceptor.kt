

package org.matrix.android.sdk.internal.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import org.matrix.android.sdk.internal.di.MatrixScope
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset
import javax.inject.Inject


@MatrixScope
internal class CurlLoggingInterceptor @Inject constructor() :
        Interceptor {

    
    var curlOptions: String? = null

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        var compressed = false

        var curlCmd = "curl"
        curlOptions?.let {
            curlCmd += " $it"
        }
        curlCmd += " -X " + request.method

        val requestBody = request.body
        if (requestBody != null) {
            if (requestBody.contentLength() > 100_000) {
                Timber.w("Unable to log curl command data, size is too big (${requestBody.contentLength()})")
                
                curlCmd += "DATA IS TOO BIG"
            } else {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                var charset: Charset? = UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }
                
                curlCmd += " --data $'" + buffer.readString(charset!!).replace("\n", "\\n") + "'"
            }
        }

        val headers = request.headers
        var i = 0
        val count = headers.size
        while (i < count) {
            val name = headers.name(i)
            val value = headers.value(i)
            if ("Accept-Encoding".equals(name, ignoreCase = true) && "gzip".equals(value, ignoreCase = true)) {
                compressed = true
            }
            curlCmd += " -H \"$name: $value\""
            i++
        }

        curlCmd += ((if (compressed) " --compressed " else " ") + "'" + request.url.toString()
                
                .replace("://10.0.2.2:8080/".toRegex(), "://127.0.0.1:8080/") +
                "'")

        
        curlCmd += " | python -m json.tool"

        Timber.d("--- cURL (${request.url})")
        Timber.d(curlCmd)

        return chain.proceed(request)
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
    }
}
