
package org.matrix.android.sdk.common

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.matrix.android.sdk.internal.session.TestInterceptor
import javax.net.ssl.HttpsURLConnection


class MockOkHttpInterceptor : TestInterceptor {

    private var rules: ArrayList<Rule> = ArrayList()

    fun addRule(rule: Rule) {
        rules.add(rule)
    }

    fun clearRules() {
        rules.clear()
    }

    override var sessionId: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        rules.forEach { rule ->
            if (originalRequest.url.toString().contains(rule.match)) {
                rule.process(originalRequest)?.let {
                    return it
                }
            }
        }

        return chain.proceed(originalRequest)
    }

    abstract class Rule(val match: String) {
        abstract fun process(originalRequest: Request): Response?
    }

    
    class SimpleRule(match: String,
                     private val code: Int = HttpsURLConnection.HTTP_OK,
                     private val body: String = "{}") : Rule(match) {

        override fun process(originalRequest: Request): Response? {
            return Response.Builder()
                    .protocol(Protocol.HTTP_1_1)
                    .request(originalRequest)
                    .message("mocked answer")
                    .body(body.toResponseBody(null))
                    .code(code)
                    .build()
        }
    }
}
